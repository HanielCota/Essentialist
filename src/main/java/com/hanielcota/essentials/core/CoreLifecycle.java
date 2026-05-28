package com.hanielcota.essentials.core;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.core.lifecycle.LifecyclePhase;
import com.hanielcota.essentials.module.environment.ModuleContext;
import com.hanielcota.essentials.module.registration.ModuleManager;
import com.hanielcota.essentials.service.ServiceRegistry;
import com.hanielcota.essentials.shared.Log;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Manages the plugin lifecycle phases. Extracted from {@link EssentialsCore} so lifecycle
 * orchestration is separate from the public API facade.
 *
 * <p>Shutdown follows the {@link ShutdownRegistry} that bootstrap stages populate — there is no
 * hard-coded teardown order here. Modules are always disabled first; subsequent steps run in
 * reverse-registration order.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class CoreLifecycle {

  private static final Log LOG = Log.of(CoreLifecycle.class);

  private final @NonNull EssentialsPlugin plugin;
  private final @NonNull ServiceRegistry services;
  private volatile LifecyclePhase phase = LifecyclePhase.BOOTING;

  LifecyclePhase phase() {
    return this.phase;
  }

  void advance(@NonNull LifecyclePhase next) {
    this.phase = next;

    if (next == LifecyclePhase.ENABLED) {
      enableModules();
    }
  }

  private void enableModules() {
    var moduleManager = this.services.resolve(ModuleManager.class);
    var context = new ModuleContext(this.plugin, this.services);

    moduleManager.enableAll(context);
  }

  void shutdown() {
    this.phase = LifecyclePhase.DISABLING;

    try {
      disableModules();
    } finally {
      runShutdownSteps();
      this.phase = LifecyclePhase.DISABLED;
    }
  }

  private void disableModules() {
    var moduleManagerHandle = this.services.find(ModuleManager.class);
    moduleManagerHandle.ifPresent(ModuleManager::disableAll);
  }

  private void runShutdownSteps() {
    var registryHandle = this.services.find(ShutdownRegistry.class);
    if (registryHandle.isEmpty()) {
      return;
    }

    var steps = new ArrayList<>(registryHandle.get().steps());
    java.util.Collections.reverse(steps);

    runSteps(steps);
  }

  private static void runSteps(@NonNull List<ShutdownStep> steps) {
    for (var step : steps) {
      safelyShutdown(step);
    }
  }

  private static void safelyShutdown(@NonNull ShutdownStep step) {
    try {
      step.run();
    } catch (RuntimeException e) {
      LOG.error(e, "{} shutdown failed", step.label());
    }
  }
}
