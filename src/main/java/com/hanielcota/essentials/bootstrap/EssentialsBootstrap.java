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
import com.hanielcota.essentials.database.DefaultSqlExecutor;
import com.hanielcota.essentials.database.SqlConnectionFactory;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.database.SqliteDatabase;
import com.hanielcota.essentials.exception.PluginException;
import com.hanielcota.essentials.module.Module;
import com.hanielcota.essentials.module.ModuleManager;
import com.hanielcota.essentials.paper.AudienceProvider;
import com.hanielcota.essentials.paper.BukkitPlayerProvider;
import com.hanielcota.essentials.paper.PaperAudienceProvider;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.scheduler.PaperScheduler;
import com.hanielcota.essentials.scheduler.Scheduler;
import com.hanielcota.essentials.service.DefaultServiceRegistry;
import com.hanielcota.essentials.service.ServiceRegistry;
import com.hanielcota.essentials.user.DefaultUserSessionService;
import com.hanielcota.essentials.user.UserSessionListener;
import com.hanielcota.essentials.user.UserSessionService;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class EssentialsBootstrap {

  private final EssentialsPlugin plugin;

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

    var core = new EssentialsCore(this.plugin, services);
    services.register(EssentialsApi.class, core);

    mirrorServicesToCommands(framework, services);

    core.advance(LifecyclePhase.ENABLED);
    return core;
  }

  private void registerScheduler(@NonNull ServiceRegistry services) {
    var scheduler = new PaperScheduler(this.plugin);
    services.register(Scheduler.class, scheduler);
  }

  private void registerPaperAdapters(@NonNull ServiceRegistry services) {
    var audienceProvider = new PaperAudienceProvider(this.plugin);
    services.register(AudienceProvider.class, audienceProvider);

    var playerProvider = new BukkitPlayerProvider(this.plugin);
    services.register(PlayerProvider.class, playerProvider);
  }

  private void registerConfigs(@NonNull ServiceRegistry services) {
    var dataFolder = this.plugin.getDataFolder();
    var configDir = dataFolder.toPath().resolve("modules");

    var configService = new YamlConfigService(configDir);
    services.register(ConfigService.class, configService);
  }

  private PaperCommandFramework registerCommands(
      @NonNull ServiceRegistry services, @NonNull ModuleManager modules) {
    var modulesList = modules.all();
    var moduleStream = modulesList.stream();
    var mappedStream =
        moduleStream.map(
            module -> (Consumer<PaperCommandFramework.Builder>) module::customizeCommands);
    var customizers = mappedStream.toList();

    var framework = new CommandBootstrap(this.plugin, customizers).createFramework();
    services.register(PaperCommandFramework.class, framework);

    return framework;
  }

  private void registerMenus(@NonNull ServiceRegistry services) {
    var menuService = MenuFramework.create(this.plugin);
    services.register(MenuService.class, menuService);
  }

  private void registerDatabase(@NonNull ServiceRegistry services) {
    var dataFolder = this.plugin.getDataFolder();
    var dbPath = dataFolder.toPath().resolve("data").resolve("essentials.db");
    var parentDir = dbPath.getParent();

    try {
      Files.createDirectories(parentDir);
    } catch (IOException e) {
      throw new PluginException("Failed to create database directory: " + parentDir, e);
    }

    var database = new SqliteDatabase(dbPath);
    database.connect();

    services.register(DatabaseProvider.class, database);
    services.register(SqlConnectionFactory.class, database);

    var sqlExecutor = new DefaultSqlExecutor(database);
    services.register(SqlExecutor.class, sqlExecutor);
  }

  private void registerUserStack(@NonNull ServiceRegistry services) {
    var sessions = new DefaultUserSessionService();
    services.register(UserSessionService.class, sessions);

    var server = this.plugin.getServer();
    var pluginManager = server.getPluginManager();
    var sessionListener = new UserSessionListener(sessions);

    pluginManager.registerEvents(sessionListener, this.plugin);
  }

  private ModuleManager createModules() {
    var modules = new ModuleManager();

    var currentClass = getClass();
    var classLoader = currentClass.getClassLoader();
    var loader = ServiceLoader.load(Module.class, classLoader);

    loader.forEach(modules::register);
    return modules;
  }

  @SuppressWarnings("unchecked")
  private void mirrorServicesToCommands(
      @NonNull PaperCommandFramework framework, @NonNull ServiceRegistry services) {
    var registeredTypes = services.registered();

    for (var type : registeredTypes) {
      if (type == PaperCommandFramework.class) {
        continue;
      }

      var castedType = (Class<Object>) type;
      var resolvedService = services.resolve(castedType);

      framework.registerDependency(castedType, resolvedService);
    }
  }
}
