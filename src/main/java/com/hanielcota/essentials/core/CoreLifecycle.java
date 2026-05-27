package com.hanielcota.essentials.core;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.core.lifecycle.LifecyclePhase;
import com.hanielcota.essentials.database.connection.DatabaseProvider;
import com.hanielcota.essentials.module.registration.ModuleManager;
import com.hanielcota.essentials.service.ServiceRegistry;
import com.hanielcota.essentials.shared.Log;
import lombok.NonNull;

/**
 * Manages the plugin lifecycle phases and the ordered shutdown sequence. Extracted from {@link
 * EssentialsCore} so lifecycle orchestration is separate from the public API facade.
 */
final class CoreLifecycle {

  private static final Log LOG = Log.of(CoreLifecycle.class);

  private final ServiceRegistry services;
  private volatile LifecyclePhase phase = LifecyclePhase.BOOTING;

  CoreLifecycle(@NonNull ServiceRegistry services) {
    this.services = services;
  }

  LifecyclePhase phase() {
    return this.phase;
  }

  void advance(@NonNull LifecyclePhase next) {
    this.phase = next;
  }

  void shutdown() {
    this.phase = LifecyclePhase.DISABLING;

    try {
      var moduleManager = this.services.resolve(ModuleManager.class);
      moduleManager.disableAll();
    } finally {
      safelyShutdown("MenuService", this::shutdownMenuService);
      safelyShutdown("DatabaseProvider", this::shutdownDatabase);

      this.phase = LifecyclePhase.DISABLED;
    }
  }

  private void shutdownMenuService() {
    var menuHandle = this.services.find(MenuService.class);
    menuHandle.ifPresent(MenuService::shutdown);
  }

  private void shutdownDatabase() {
    var databaseHandle = this.services.find(DatabaseProvider.class);
    databaseHandle.ifPresent(DatabaseProvider::close);
  }

  private static void safelyShutdown(@NonNull String label, @NonNull Runnable step) {
    try {
      step.run();
    } catch (RuntimeException e) {
      LOG.error(e, "{} shutdown failed", label);
    }
  }
}
