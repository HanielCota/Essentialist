package com.hanielcota.essentials.bootstrap;

import com.github.hanielcota.menuframework.MenuFramework;
import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.api.EssentialsApi;
import com.hanielcota.essentials.command.ActorMessages;
import com.hanielcota.essentials.command.CommandBootstrap;
import com.hanielcota.essentials.command.CommandRegistrar;
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
import com.hanielcota.essentials.paper.AudienceProvider;
import com.hanielcota.essentials.paper.BukkitPlayerProvider;
import com.hanielcota.essentials.paper.DefaultTaskDispatcher;
import com.hanielcota.essentials.paper.PaperAudienceProvider;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.paper.TaskDispatcher;
import com.hanielcota.essentials.scheduler.PaperScheduler;
import com.hanielcota.essentials.scheduler.Scheduler;
import com.hanielcota.essentials.serialization.LocationSerializer;
import com.hanielcota.essentials.serialization.SerializerRegistry;
import com.hanielcota.essentials.service.DefaultServiceRegistry;
import com.hanielcota.essentials.service.ServiceRegistry;
import com.hanielcota.essentials.user.InMemoryUserRepository;
import com.hanielcota.essentials.user.UserRepository;
import com.hanielcota.essentials.user.UserService;
import com.hanielcota.essentials.user.UserSessionListener;
import com.hanielcota.essentials.user.UserSessionService;
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

  private static void mirrorServicesAsCommandDependencies(
      PaperCommandFramework framework, ServiceRegistry services) {
    for (Class<?> type : services.registered()) {
      if (type != PaperCommandFramework.class && type != CommandRegistrar.class) {
        mirrorOne(framework, services, type);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> void mirrorOne(
      PaperCommandFramework framework, ServiceRegistry services, Class<?> rawType) {
    Class<T> type = (Class<T>) rawType;
    framework.registerDependency(type, services.resolve(type));
  }

  public EssentialsCore start() {
    ServiceRegistry services = new DefaultServiceRegistry();

    Scheduler scheduler = registerScheduler(services);
    registerPaperAdapters(services, scheduler);
    registerConfigs(services);
    registerMessages(services);
    registerDatabase(services);
    ModuleManager modules = createModules();
    CommandRegistrar commands = registerCommands(services, modules);
    registerMenus(services);
    registerEventBus(services);
    registerUserStack(services);
    registerSerializers(services);
    registerIntegrations(services);
    services.register(ModuleManager.class, modules);

    var core = new EssentialsCore(plugin, services);
    services.register(EssentialsApi.class, core);

    mirrorServicesAsCommandDependencies(commands.framework(), services);

    core.advance(LifecyclePhase.ENABLED);
    return core;
  }

  private Scheduler registerScheduler(ServiceRegistry services) {
    Scheduler scheduler = new PaperScheduler(plugin);
    services.register(Scheduler.class, scheduler);
    return scheduler;
  }

  private void registerPaperAdapters(ServiceRegistry services, Scheduler scheduler) {
    services.register(AudienceProvider.class, new PaperAudienceProvider(plugin));
    services.register(PlayerProvider.class, new BukkitPlayerProvider(plugin));
    services.register(TaskDispatcher.class, new DefaultTaskDispatcher(scheduler));
  }

  private ConfigService registerConfigs(ServiceRegistry services) {
    ConfigService configs = new YamlConfigService(plugin.getDataFolder().toPath());
    services.register(ConfigService.class, configs);
    return configs;
  }

  private MessageService registerMessages(ServiceRegistry services) {
    var provider = new InMemoryMessageProvider(Locale.of("en"));
    var messages = new MessageService(provider);
    services.register(MessageProvider.class, provider);
    services.register(MutableMessageProvider.class, provider);
    services.register(MessageService.class, messages);
    return messages;
  }

  private CommandRegistrar registerCommands(ServiceRegistry services, ModuleManager modules) {
    var customizers =
        modules.all().stream()
            .<Consumer<PaperCommandFramework.Builder>>map(module -> module::customizeCommands)
            .toList();
    var commandBootstrap = new CommandBootstrap(plugin, customizers);
    PaperCommandFramework framework = commandBootstrap.createFramework();
    CommandRegistrar registrar = commandBootstrap.createRegistrar(framework);
    services.register(PaperCommandFramework.class, framework);
    services.register(ActorMessages.class, new ActorMessages(framework));
    services.register(CommandRegistrar.class, registrar);
    return registrar;
  }

  private MenuService registerMenus(ServiceRegistry services) {
    MenuService menus = MenuFramework.create(plugin);
    services.register(MenuService.class, menus);
    return menus;
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
    UserRepository repository = new InMemoryUserRepository();
    UserSessionService sessions = new UserSessionService();
    services.register(UserRepository.class, repository);
    services.register(UserService.class, new UserService(repository));
    services.register(UserSessionService.class, sessions);
    plugin.getServer().getPluginManager().registerEvents(new UserSessionListener(sessions), plugin);
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
}
