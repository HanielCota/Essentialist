package com.hanielcota.essentials.module.control;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.config.YamlReadWriter;
import com.hanielcota.essentials.module.discovery.ModuleSettings;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ModuleControlTest {

  @TempDir private Path tempDir;

  private ModuleControl control(Set<String> running, Map<String, Boolean> boot) {
    var file = this.tempDir.resolve("modules.yml");
    return new ModuleControl(file, List.of("alpha", "beta"), running, boot, Runnable::run);
  }

  @Test
  void reflectsBootStateWithNoPendingChange() {
    var control = control(Set.of("alpha"), Map.of("alpha", true, "beta", false));

    assertTrue(control.persistedEnabled("alpha"));
    assertTrue(control.runningEnabled("alpha"));
    assertFalse(control.pendingRestart("alpha"));

    assertFalse(control.persistedEnabled("beta"));
    assertFalse(control.runningEnabled("beta"));
    assertFalse(control.pendingRestart("beta"));
  }

  @Test
  void missingEntryDefaultsToEnabled() {
    var control = control(Set.of("alpha"), Map.of());

    assertTrue(control.persistedEnabled("alpha"));
  }

  @Test
  void toggleFlipsStateAndMarksPendingRestart() {
    var control = control(Set.of("alpha", "beta"), Map.of("alpha", true, "beta", true));

    var next = control.toggle("alpha");

    assertFalse(next);
    assertFalse(control.persistedEnabled("alpha"));
    assertTrue(control.runningEnabled("alpha"));
    assertTrue(control.pendingRestart("alpha"));
  }

  @Test
  void protectsCoreModulesFromToggling() {
    var control = control(Set.of("alpha"), Map.of());

    assertTrue(control.isProtected("essentials"));
    assertFalse(control.isProtected("alpha"));
  }

  @Test
  void togglePersistsToDisk() {
    var control = control(Set.of("alpha", "beta"), Map.of("alpha", true, "beta", true));

    control.toggle("beta");

    var file = this.tempDir.resolve("modules.yml");
    var reloaded = YamlReadWriter.readMerging(file, ModuleSettings.class, ModuleSettings::defaults);

    assertFalse(reloaded.enabled("beta"));
    assertTrue(reloaded.enabled("alpha"));
  }
}
