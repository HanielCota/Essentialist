package com.hanielcota.essentials.module;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.exception.ModuleLoadException;
import com.hanielcota.essentials.module.environment.ModuleContext;
import com.hanielcota.essentials.module.lifecycle.ModuleLifecycle;
import com.hanielcota.essentials.module.registry.ModuleRegistry;
import com.hanielcota.essentials.module.registry.ModuleState;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import org.junit.jupiter.api.Test;

class ModuleLifecycleTest {

  @Test
  void enableAllRunsEveryModuleAndMarksThemEnabled() {
    var registry = new ModuleRegistry();
    var trace = new ArrayList<String>();
    var first = recording("a", trace);
    var second = recording("b", trace);
    registry.register(first);
    registry.register(second);

    var lifecycle = new ModuleLifecycle(registry);
    lifecycle.enableAll(noContext());

    assertEquals(List.of("enable:a", "enable:b"), trace);
    assertEquals(ModuleState.ENABLED, registry.stateOf("a"));
    assertEquals(ModuleState.ENABLED, registry.stateOf("b"));
  }

  @Test
  void enableFailureRollsBackPreviouslyEnabledModulesInReverseOrder() {
    var registry = new ModuleRegistry();
    var trace = new ArrayList<String>();
    var ok = recording("a", trace);
    var failing = failing("b", trace);
    var unreached = recording("c", trace);
    registry.register(ok);
    registry.register(failing);
    registry.register(unreached);

    var lifecycle = new ModuleLifecycle(registry);
    var exception = assertThrows(ModuleLoadException.class, () -> lifecycle.enableAll(noContext()));

    assertTrue(exception.getMessage().contains("b"), exception.getMessage());
    assertEquals(List.of("enable:a", "enable-fail:b", "disable:a"), trace);
    assertEquals(ModuleState.DISABLED, registry.stateOf("a"));
    assertEquals(ModuleState.FAILED, registry.stateOf("b"));
    assertEquals(ModuleState.REGISTERED, registry.stateOf("c"));
  }

  @Test
  void disableAllRunsEnabledModulesInReverseOrder() {
    var registry = new ModuleRegistry();
    var trace = new ArrayList<String>();
    var a = recording("a", trace);
    var b = recording("b", trace);
    var c = recording("c", trace);
    registry.register(a);
    registry.register(b);
    registry.register(c);

    var lifecycle = new ModuleLifecycle(registry);
    lifecycle.enableAll(noContext());
    trace.clear();

    lifecycle.disableAll();

    assertEquals(List.of("disable:c", "disable:b", "disable:a"), trace);
    assertEquals(ModuleState.DISABLED, registry.stateOf("a"));
    assertEquals(ModuleState.DISABLED, registry.stateOf("c"));
  }

  @Test
  void disableAllSkipsModulesThatNeverEnabled() {
    var registry = new ModuleRegistry();
    var trace = new ArrayList<String>();
    var enabled = recording("a", trace);
    registry.register(enabled);

    var lifecycle = new ModuleLifecycle(registry);
    lifecycle.enableAll(noContext());
    trace.clear();

    // Force a not-enabled state on the module so disableAll has to skip it.
    registry.markState("a", ModuleState.FAILED);
    lifecycle.disableAll();

    assertTrue(trace.isEmpty(), "should not call disable on a failed module");
  }

  private static Module recording(@NonNull String id, @NonNull List<String> trace) {
    return new RecordingModule(id, trace, false);
  }

  private static Module failing(@NonNull String id, @NonNull List<String> trace) {
    return new RecordingModule(id, trace, true);
  }

  private static ModuleContext noContext() {
    return new ModuleContext(null, null);
  }

  private static final class RecordingModule implements Module {

    private final ModuleMetadata metadata;
    private final List<String> trace;
    private final boolean failOnEnable;

    RecordingModule(@NonNull String id, @NonNull List<String> trace, boolean failOnEnable) {
      this.metadata = new ModuleMetadata(id, Set.of(), "1.0", "");
      this.trace = trace;
      this.failOnEnable = failOnEnable;
    }

    @Override
    public ModuleMetadata metadata() {
      return this.metadata;
    }

    @Override
    public void enable(@NonNull ModuleContext context) {
      if (this.failOnEnable) {
        this.trace.add("enable-fail:" + this.metadata.id());
        throw new IllegalStateException("intentional");
      }
      this.trace.add("enable:" + this.metadata.id());
    }

    @Override
    public void disable() {
      this.trace.add("disable:" + this.metadata.id());
    }
  }
}
