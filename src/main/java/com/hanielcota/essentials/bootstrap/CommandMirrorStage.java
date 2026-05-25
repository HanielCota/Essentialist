package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.service.ServiceRegistry;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;

/**
 * Mirrors infrastructure services (Scheduler, ConfigService, DatabaseProvider, MenuService,
 * EssentialsApi, ModuleManager, …) registered before the framework existed so command handlers can
 * request them via constructor injection. Services registered after this — i.e. all module-owned
 * services — are auto-mirrored by {@code ModuleServices.register} as they appear.
 */
final class CommandMirrorStage implements BootstrapStage {

  @Override
  public String name() {
    return "command-mirror";
  }

  @Override
  public void start(@NonNull StageContext context) {
    var services = context.services();
    var framework = services.resolve(PaperCommandFramework.class);

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
