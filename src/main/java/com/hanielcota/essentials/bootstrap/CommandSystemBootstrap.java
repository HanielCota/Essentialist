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
    var customizers =
        modules.all().stream()
            .map(module -> (Consumer<PaperCommandFramework.Builder>) module::customizeCommands)
            .toList();

    var framework = new CommandBootstrap(this.plugin, customizers).createFramework();
    services.register(PaperCommandFramework.class, framework);

    return framework;
  }

  @SuppressWarnings("unchecked")
  void mirrorServicesToCommands(
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
