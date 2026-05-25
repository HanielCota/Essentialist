package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.command.CommandBootstrap;
import com.hanielcota.essentials.module.ModuleManager;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Builds the {@link PaperCommandFramework}, applies every enabled module's command customizer, and
 * publishes the framework as a service. Mirroring already-registered infrastructure services to the
 * framework's dependency table is a separate stage ({@link CommandMirrorStage}) so it runs after
 * the rest of the registry has filled up.
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
    var modules = services.resolve(ModuleManager.class);

    var allModules = modules.all();
    var modulesStream = allModules.stream();

    var customizers =
        modulesStream
            .map(module -> (Consumer<PaperCommandFramework.Builder>) module::customizeCommands)
            .toList();

    var commandBootstrap = new CommandBootstrap(this.plugin, customizers);
    var framework = commandBootstrap.createFramework();

    services.register(PaperCommandFramework.class, framework);
  }
}
