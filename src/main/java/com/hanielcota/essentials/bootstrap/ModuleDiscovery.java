package com.hanielcota.essentials.bootstrap;

import com.hanielcota.essentials.module.Module;
import com.hanielcota.essentials.module.ModuleManager;
import java.util.ServiceLoader;

final class ModuleDiscovery {

  ModuleManager discover() {
    var modules = new ModuleManager();
    var classLoader = getClass().getClassLoader();
    var loader = ServiceLoader.load(Module.class, classLoader);

    loader.forEach(modules::register);
    return modules;
  }
}
