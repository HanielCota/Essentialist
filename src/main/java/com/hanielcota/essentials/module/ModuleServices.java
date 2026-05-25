package com.hanielcota.essentials.module;

import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

final class ModuleServices {

  private final List<Class<?>> owned = new ArrayList<>();

  <T> void register(@NonNull ModuleContext context, @NonNull Class<T> type, @NonNull T instance) {
    var services = context.services();

    services.unregister(type);
    services.register(type, instance);
    this.owned.add(type);

    var commandFrameworkOpt = services.find(PaperCommandFramework.class);
    if (commandFrameworkOpt.isEmpty()) {
      return;
    }

    var framework = commandFrameworkOpt.get();
    framework.registerDependency(type, instance);
  }

  void unregisterOwned(ModuleContext context) {
    if (context == null) {
      this.owned.clear();
      return;
    }

    var services = context.services();
    for (var type : this.owned) {
      services.unregister(type);
    }
    this.owned.clear();
  }
}
