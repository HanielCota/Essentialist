package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.module.Module;
import com.hanielcota.essentials.module.ModuleFilter;
import com.hanielcota.essentials.module.ModuleManager;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ServiceLoader;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class ModuleDiscovery {

  private final @NonNull Path dataFolder;

  ModuleManager discover() {
    var modules = new ModuleManager();
    var classLoader = getClass().getClassLoader();
    var loader = ServiceLoader.load(Module.class, classLoader);
    var discovered = new ArrayList<Module>();

    loader.forEach(discovered::add);

    var settingsLoader = new ModuleSettingsLoader(this.dataFolder);
    var settings = settingsLoader.load(discovered);
    var enabledModules = ModuleFilter.enabled(discovered, settings);

    enabledModules.forEach(modules::register);
    return modules;
  }
}
