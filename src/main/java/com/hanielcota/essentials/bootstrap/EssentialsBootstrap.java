package com.hanielcota.essentials.bootstrap;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.api.EssentialsApi;
import com.hanielcota.essentials.command.CommandBootstrap;
import com.hanielcota.essentials.config.ConfigService;
import com.hanielcota.essentials.config.YamlConfigService;
import com.hanielcota.essentials.core.EssentialsCore;
import com.hanielcota.essentials.core.lifecycle.LifecyclePhase;
import com.hanielcota.essentials.database.DatabaseProvider;
import com.hanielcota.essentials.database.SqliteDatabase;
import com.hanielcota.essentials.module.Module;
import com.hanielcota.essentials.module.ModuleManager;
import com.hanielcota.essentials.paper.*;
import com.hanielcota.essentials.scheduler.PaperScheduler;
import com.hanielcota.essentials.scheduler.Scheduler;
import com.hanielcota.essentials.service.DefaultServiceRegistry;
import com.hanielcota.essentials.service.ServiceRegistry;
import com.hanielcota.essentials.user.*;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.function.Consumer;

public final class EssentialsBootstrap {

  private final EssentialsPlugin plugin;

  public EssentialsBootstrap(EssentialsPlugin plugin) {
    this.plugin = Objects.requireNonNull(plugin, "plugin");
  }

  public EssentialsCore start() {
    var services = new DefaultServiceRegistry();

    registerScheduler(services);
    registerPaperAdapters(services);
    registerConfigs(services);
    registerDatabase(services);

    var modules = createModules();
    services.register(ModuleManager.class, modules);

    var framework = registerCommands(services, modules);
    registerMenus(services);
    registerUserStack(services);

    var core = new EssentialsCore(plugin, services);
    services.register(EssentialsApi.class, core);

    mirrorServicesToCommands(framework, services);

    core.advance(LifecyclePhase.ENABLED);
    return core;
  }

  private void registerScheduler(ServiceRegistry services) {
    services.register(Scheduler.class, new PaperScheduler(plugin));
  }

  private void registerPaperAdapters(ServiceRegistry services) {
    services.register(AudienceProvider.class, new PaperAudienceProvider(plugin));
    services.register(PlayerProvider.class, new BukkitPlayerProvider(plugin));
  }

  private void registerConfigs(ServiceRegistry services) {
    services.register(ConfigService.class, new YamlConfigService(plugin.getDataFolder().toPath()));
  }

  private PaperCommandFramework registerCommands(ServiceRegistry services, ModuleManager modules) {
    var customizers =
        modules.all().stream()
            .map(module -> (Consumer<PaperCommandFramework.Builder>) module::customizeCommands)
            .toList();

    var framework = new CommandBootstrap(plugin, customizers).createFramework();
    services.register(PaperCommandFramework.class, framework);
    return framework;
  }

  private void registerMenus(ServiceRegistry services) {
    services.register(MenuService.class, MenuFramework.create(plugin));
  }

  private void registerDatabase(ServiceRegistry services) {
    var database = new SqliteDatabase(plugin.getDataFolder().toPath().resolve("essentials.db"));
    database.connect();
    services.register(DatabaseProvider.class, database);
  }

  private void registerUserStack(ServiceRegistry services) {
    var sessions = new UserSessionService();
    services.register(UserSessionService.class, sessions);

    plugin.getServer().getPluginManager().registerEvents(new UserSessionListener(sessions), plugin);
  }

  private ModuleManager createModules() {
    var modules = new ModuleManager();
    ServiceLoader.load(Module.class, getClass().getClassLoader()).forEach(modules::register);
    return modules;
  }

  @SuppressWarnings("unchecked")
  private void mirrorServicesToCommands(PaperCommandFramework framework, ServiceRegistry services) {
    for (Class<?> type : services.registered()) {
      if (type != PaperCommandFramework.class) {
        framework.registerDependency((Class<Object>) type, services.resolve((Class<Object>) type));
      }
    }
  }
}
