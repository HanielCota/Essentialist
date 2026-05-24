package com.hanielcota.essentials.bootstrap;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.api.EssentialsApi;
import com.hanielcota.essentials.core.EssentialsCore;
import com.hanielcota.essentials.core.lifecycle.LifecyclePhase;
import com.hanielcota.essentials.database.DatabaseProvider;
import com.hanielcota.essentials.module.ModuleManager;
import com.hanielcota.essentials.service.DefaultServiceRegistry;
import com.hanielcota.essentials.service.ServiceRegistry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class EssentialsBootstrap {

  private final EssentialsPlugin plugin;

  public EssentialsCore start() {
    var services = new DefaultServiceRegistry();
    EssentialsCore core = null;
    try {
      new CoreServicesBootstrap(this.plugin).register(services);
      new DatabaseBootstrap(this.plugin).register(services);

      var modules = new ModuleDiscovery().discover();
      services.register(ModuleManager.class, modules);

      var commands = new CommandSystemBootstrap(this.plugin);
      var framework = commands.register(services, modules);
      new MenuBootstrap(this.plugin).register(services);
      new UserStackBootstrap(this.plugin).register(services);

      core = new EssentialsCore(this.plugin, services);
      services.register(EssentialsApi.class, core);

      commands.mirrorServicesToCommands(framework, services);

      core.advance(LifecyclePhase.ENABLED);
      return core;
    } catch (RuntimeException e) {
      rollback(services, core, e);
      throw e;
    }
  }

  private static void rollback(
      ServiceRegistry services, EssentialsCore core, RuntimeException cause) {
    if (core != null) {
      suppressAndRun(cause, core::shutdown);
      return;
    }
    // Pre-core failure: close any infra already registered so the JVM doesn't
    // keep the HikariCP pool / MenuService executor alive after onEnable bails.
    services.find(DatabaseProvider.class).ifPresent(db -> suppressAndRun(cause, db::close));
    services.find(MenuService.class).ifPresent(menu -> suppressAndRun(cause, menu::shutdown));
  }

  private static void suppressAndRun(RuntimeException primary, Runnable cleanup) {
    try {
      cleanup.run();
    } catch (RuntimeException e) {
      primary.addSuppressed(e);
    }
  }
}
