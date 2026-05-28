package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.command.CommandBootstrap;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.FrameworkActorFactory;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import io.papermc.paper.registry.RegistryAccess;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Builds the {@link PaperCommandFramework} and publishes the framework as a service. Mirroring
 * already-registered infrastructure services to the framework's dependency table is a separate
 * stage ({@link CommandMirrorStage}) so it runs after the rest of the registry has filled up.
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
    var commandBootstrap = new CommandBootstrap(this.plugin, registryAccess);
    var framework = commandBootstrap.createFramework();

    services.register(PaperCommandFramework.class, framework);
    services.register(ActorFactory.class, new FrameworkActorFactory(framework));
  }
}
