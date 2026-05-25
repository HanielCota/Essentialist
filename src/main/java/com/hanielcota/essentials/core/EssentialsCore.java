package com.hanielcota.essentials.core;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.api.EssentialsApi;
import com.hanielcota.essentials.core.lifecycle.LifecyclePhase;
import com.hanielcota.essentials.database.DatabaseProvider;
import com.hanielcota.essentials.module.ModuleContext;
import com.hanielcota.essentials.module.ModuleManager;
import com.hanielcota.essentials.service.ServiceRegistry;
import com.hanielcota.essentials.util.Log;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class EssentialsCore implements EssentialsApi {

  private static final Log LOG = Log.of(EssentialsCore.class);

  private final EssentialsPlugin plugin;
  private final ServiceRegistry services;

  private volatile LifecyclePhase phase = LifecyclePhase.BOOTING;

  public void advance(@NonNull LifecyclePhase next) {
    this.phase = next;

    if (next == LifecyclePhase.ENABLED) {
      var moduleManager = this.services.resolve(ModuleManager.class);
      var context = newContext();

      moduleManager.enableAll(context);
    }
  }

  public void shutdown() {
    this.phase = LifecyclePhase.DISABLING;

    try {
      var moduleManager = this.services.resolve(ModuleManager.class);
      moduleManager.disableAll();
    } finally {
      // Shut down MenuService before the database: menu teardown closes open viewers via
      // InventoryCloseEvent listeners, some of which (e.g. invsee release, homes session cleanup)
      // may still touch services that hit SQL. Both steps are wrapped so a thrown MenuService
      // shutdown never strands the HikariCP pool / SQLite file open.
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

  public LifecyclePhase phase() {
    return this.phase;
  }

  @Override
  public EssentialsPlugin plugin() {
    return this.plugin;
  }

  @Override
  public ServiceRegistry services() {
    return this.services;
  }

  private ModuleContext newContext() {
    return new ModuleContext(this.plugin, this.services);
  }
}
