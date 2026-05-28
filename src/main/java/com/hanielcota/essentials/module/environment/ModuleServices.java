package com.hanielcota.essentials.module.environment;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

public final class ModuleServices {

  private final List<Class<?>> owned = new ArrayList<>();

  public <T> void register(
      @NonNull ModuleContext context, @NonNull Class<T> type, @NonNull T instance) {
    var services = context.services();

    // Let the registry's duplicate-detection surface conflicts. Silently overwriting hides the
    // case where two modules both claim the same service type — order of module enable then
    // decides the winner, which is invisible at runtime.
    services.register(type, instance);
    this.owned.add(type);
  }

  public void unregisterOwned(ModuleContext context) {
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
