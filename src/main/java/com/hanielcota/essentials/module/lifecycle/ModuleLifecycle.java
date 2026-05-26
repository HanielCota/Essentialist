package com.hanielcota.essentials.module.lifecycle;

import com.hanielcota.essentials.exception.ModuleLoadException;
import com.hanielcota.essentials.module.Module;
import com.hanielcota.essentials.module.discovery.ModuleDependencyResolver;
import com.hanielcota.essentials.module.environment.ModuleContext;
import com.hanielcota.essentials.module.registry.ModuleRegistry;
import com.hanielcota.essentials.module.registry.ModuleState;
import com.hanielcota.essentials.shared.Log;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Drives the enable/disable transitions of registered modules in the order produced by {@link
 * ModuleDependencyResolver}. A failure during {@code enableAll} triggers a reverse-order rollback
 * of the already-enabled modules and then propagates the {@link ModuleLoadException}.
 */
@RequiredArgsConstructor
public final class ModuleLifecycle {

  private static final Log LOG = Log.of(ModuleLifecycle.class);

  private final ModuleRegistry registry;

  private List<Module> enableOrder = List.of();

  public void enableAll(@NonNull ModuleContext context) {
    this.enableOrder = ModuleDependencyResolver.resolve(this.registry.all());

    var size = this.enableOrder.size();
    var succeeded = new ArrayList<Module>(size);

    for (var module : this.enableOrder) {
      var moduleId = module.id();
      try {
        module.enable(context);
        this.registry.markState(moduleId, ModuleState.ENABLED);
        succeeded.add(module);
      } catch (RuntimeException e) {
        this.registry.markState(moduleId, ModuleState.FAILED);
        rollback(succeeded);
        throw new ModuleLoadException(moduleId, "enable() failed", e);
      }
    }
  }

  public void disableAll() {
    for (var module : this.enableOrder.reversed()) {
      var moduleId = module.id();
      var currentState = this.registry.stateOf(moduleId);
      if (currentState != ModuleState.ENABLED) {
        continue;
      }

      try {
        module.disable();
        this.registry.markState(moduleId, ModuleState.DISABLED);
      } catch (RuntimeException e) {
        this.registry.markState(moduleId, ModuleState.FAILED);
        LOG.error(e, "Module disable failed: {}", moduleId);
      }
    }
  }

  private void rollback(@NonNull List<Module> succeeded) {
    var lastIndex = succeeded.size() - 1;

    for (var i = lastIndex; i >= 0; i--) {
      var module = succeeded.get(i);
      var moduleId = module.id();

      try {
        module.disable();
        this.registry.markState(moduleId, ModuleState.DISABLED);
      } catch (RuntimeException e) {
        this.registry.markState(moduleId, ModuleState.FAILED);
        LOG.error(e, "Rollback disable failed: {}", moduleId);
      }
    }
  }
}
