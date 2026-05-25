package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.command.CommandBootstrap;
import com.hanielcota.essentials.module.ModuleManager;
import com.hanielcota.essentials.service.ServiceRegistry;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class CommandSystemBootstrap {

  private final EssentialsPlugin plugin;

  PaperCommandFramework register(
      @NonNull ServiceRegistry services, @NonNull ModuleManager modules) {
    var allModules = modules.all();
    var modulesStream = allModules.stream();

    var customizers =
        modulesStream
            .map(module -> (Consumer<PaperCommandFramework.Builder>) module::customizeCommands)
            .toList();

    var commandBootstrap = new CommandBootstrap(this.plugin, customizers);
    var framework = commandBootstrap.createFramework();

    services.register(PaperCommandFramework.class, framework);

    return framework;
  }

  /**
   * Mirrors infrastructure services (Scheduler, ConfigService, DatabaseProvider, MenuService,
   * EssentialsApi, ModuleManager, …) registered before the framework existed so command handlers
   * can request them via constructor injection. Services registered after this — i.e. all
   * module-owned services — are auto-mirrored by {@code ModuleServices.register} as they appear.
   */
  void mirrorServicesToCommands(
      @NonNull PaperCommandFramework framework, @NonNull ServiceRegistry services) {
    var registeredTypes = services.registered();

    for (var type : registeredTypes) {
      if (type == PaperCommandFramework.class) {
        continue;
      }

      mirrorOne(framework, services, type);
    }
  }

  private <T> void mirrorOne(
      @NonNull PaperCommandFramework framework,
      @NonNull ServiceRegistry services,
      @NonNull Class<T> type) {
    var instance = services.resolve(type);
    framework.registerDependency(type, instance);
  }
}
