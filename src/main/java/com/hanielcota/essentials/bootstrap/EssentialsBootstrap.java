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
import com.hanielcota.essentials.event.EventBus;
import com.hanielcota.essentials.event.SimpleEventBus;
import com.hanielcota.essentials.integrations.IntegrationRegistry;
import com.hanielcota.essentials.message.InMemoryMessageProvider;
import com.hanielcota.essentials.message.MessageProvider;
import com.hanielcota.essentials.message.MessageService;
import com.hanielcota.essentials.message.MutableMessageProvider;
import com.hanielcota.essentials.module.Module;
import com.hanielcota.essentials.module.ModuleManager;
import com.hanielcota.essentials.paper.*;
import com.hanielcota.essentials.scheduler.PaperScheduler;
import com.hanielcota.essentials.scheduler.Scheduler;
import com.hanielcota.essentials.serialization.LocationSerializer;
import com.hanielcota.essentials.serialization.SerializerRegistry;
import com.hanielcota.essentials.service.DefaultServiceRegistry;
import com.hanielcota.essentials.service.ServiceRegistry;
import com.hanielcota.essentials.user.*;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.Locale;
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

    var scheduler = registerScheduler(services);
    registerPaperAdapters(services, scheduler);
    registerConfigs(services);
    registerMessages(services);
    registerDatabase(services);

    var modules = createModules();
    services.register(ModuleManager.class, modules);

    var framework = registerCommands(services, modules);
    registerMenus(services);
    registerEventBus(services);
    registerUserStack(services);
    registerSerializers(services);
    registerIntegrations(services);

    var core = new EssentialsCore(plugin, services);
    services.register(EssentialsApi.class, core);

    mirrorServicesToCommands(framework, services);

    core.advance(LifecyclePhase.ENABLED);
    return core;
  }

  private Scheduler registerScheduler(ServiceRegistry services) {
    var scheduler = new PaperScheduler(plugin);
    services.register(Scheduler.class, scheduler);
    return scheduler;
  }

  private void registerPaperAdapters(ServiceRegistry services, Scheduler scheduler) {
    services.register(AudienceProvider.class, new PaperAudienceProvider(plugin));
    services.register(PlayerProvider.class, new BukkitPlayerProvider(plugin));
    services.register(TaskDispatcher.class, new DefaultTaskDispatcher(scheduler));
  }

  private void registerConfigs(ServiceRegistry services) {
    services.register(ConfigService.class, new YamlConfigService(plugin.getDataFolder().toPath()));
  }

  private void registerMessages(ServiceRegistry services) {
    var provider = new InMemoryMessageProvider(Locale.of("en"));
    services.register(MessageProvider.class, provider);
    services.register(MutableMessageProvider.class, provider);
    services.register(MessageService.class, new MessageService(provider));
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

  private void registerEventBus(ServiceRegistry services) {
    services.register(EventBus.class, new SimpleEventBus());
  }

  private void registerUserStack(ServiceRegistry services) {
    var repository = new InMemoryUserRepository();
    var users = new UserService(repository);
    var sessions = new UserSessionService();

    services.register(UserRepository.class, repository);
    services.register(UserService.class, users);
    services.register(UserSessionService.class, sessions);

    plugin
        .getServer()
        .getPluginManager()
        .registerEvents(new UserSessionListener(users, sessions), plugin);
  }

  private void registerSerializers(ServiceRegistry services) {
    var registry = new SerializerRegistry();
    registry.register(new LocationSerializer());
    services.register(SerializerRegistry.class, registry);
  }

  private void registerIntegrations(ServiceRegistry services) {
    services.register(IntegrationRegistry.class, new IntegrationRegistry());
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
