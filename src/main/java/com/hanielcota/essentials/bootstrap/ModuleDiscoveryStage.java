package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.module.Module;
import com.hanielcota.essentials.module.discovery.ModuleFilter;
import com.hanielcota.essentials.module.registration.ModuleManager;
import java.util.ArrayList;
import java.util.ServiceLoader;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Discovers modules via {@link ServiceLoader}, filters out anything disabled in {@code
 * modules.yml}, and publishes the resulting {@link ModuleManager} as a service.
 */
@RequiredArgsConstructor
final class ModuleDiscoveryStage implements BootstrapStage {

  private final EssentialsPlugin plugin;

  @Override
  public String name() {
    return "module-discovery";
  }

  @Override
  public void start(@NonNull StageContext context) {
    var dataFolder = this.plugin.getDataFolder().toPath();
    var modules = new ModuleManager();
    var classLoader = getClass().getClassLoader();
    var loader = ServiceLoader.load(Module.class, classLoader);
    var discovered = new ArrayList<Module>();

    loader.forEach(discovered::add);

    var settingsLoader = new ModuleSettingsLoader(dataFolder);
    var settings = settingsLoader.load(discovered);
    var enabledModules = ModuleFilter.enabled(discovered, settings);

    enabledModules.forEach(modules::register);

    context.services().register(ModuleManager.class, modules);
  }
}
