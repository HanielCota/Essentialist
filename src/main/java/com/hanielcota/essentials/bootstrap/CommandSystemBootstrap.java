package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.command.CommandBootstrap;
import com.hanielcota.essentials.command.cooldown.CooldownsConfig;
import com.hanielcota.essentials.config.ConfigService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.FrameworkActorFactory;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import io.papermc.paper.registry.RegistryAccess;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Builds the {@link PaperCommandFramework}, publishes it as a service, and wires the registry
 * listener that mirrors every subsequently-registered service into the framework's dependency
 * table. Services registered before this stage are replayed once.
 */
@RequiredArgsConstructor
final class CommandSystemBootstrap implements BootstrapStage {

  private final EssentialsPlugin plugin;

  @Override
  public String name() {
    return "command-framework";
  }

  @Override
  public void start(@NonNull StageContext context) {
    var services = context.services();

    var registryAccess = RegistryAccess.registryAccess();
    var configService = services.resolve(ConfigService.class);
    var cooldownConfig =
        configService.load("cooldowns", CooldownsConfig.class, CooldownsConfig::defaults);
    var commandBootstrap = new CommandBootstrap(this.plugin, registryAccess, cooldownConfig);
    var framework = commandBootstrap.createFramework();

    services.register(PaperCommandFramework.class, framework);
    services.register(ActorFactory.class, new FrameworkActorFactory(framework));

    replayExisting(services, framework);
    services.addRegistrationListener((type, instance) -> mirror(framework, type, instance));
  }

  private static void replayExisting(
      @NonNull com.hanielcota.essentials.service.ServiceRegistry services,
      @NonNull PaperCommandFramework framework) {
    var existing = services.registered();
    for (var type : existing) {
      if (type == PaperCommandFramework.class) {
        continue;
      }
      var instance = services.resolve(type);
      mirror(framework, type, instance);
    }
  }

  @SuppressWarnings("unchecked")
  private static <T> void mirror(
      @NonNull PaperCommandFramework framework, @NonNull Class<?> type, @NonNull Object instance) {
    framework.registerDependency((Class<T>) type, (T) instance);
  }
}
