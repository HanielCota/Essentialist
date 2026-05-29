package com.hanielcota.essentials.module.environment;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

/**
 * Tracks the service types a single module registered so they can be removed when it disables —
 * keeping a module's footprint on the shared {@link
 * com.hanielcota.essentials.service.ServiceRegistry} self-contained.
 */
public final class ModuleServices {

  private final List<Class<?>> owned = new ArrayList<>();

  /**
   * Registers {@code instance} on the shared registry and records {@code type} as owned by this
   * module. Does not swallow duplicates — a clashing type surfaces as the registry's {@link
   * IllegalStateException} so two modules can't silently fight over one service type.
   */
  public <T> void register(
      @NonNull ModuleContext context, @NonNull Class<T> type, @NonNull T instance) {
    var services = context.services();

    // Let the registry's duplicate-detection surface conflicts. Silently overwriting hides the
    // case where two modules both claim the same service type — order of module enable then
    // decides the winner, which is invisible at runtime.
    services.register(type, instance);
    this.owned.add(type);
  }

  /**
   * Unregisters every service this module owns and clears the tracking set. A {@code null} context
   * — which happens when disable runs before enable ever completed — only clears the local
   * tracking, since there is nothing to remove from the registry.
   */
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
