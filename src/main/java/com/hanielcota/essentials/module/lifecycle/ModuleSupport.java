package com.hanielcota.essentials.module.lifecycle;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleServices;
import lombok.NonNull;

/**
 * Bundle of the per-module bookkeeping helpers an {@link AbstractModule} carries: listener
 * registrations, scheduled closeables, published services and menus. Created once per module
 * instance via {@link #create()}; tests can pass a custom instance to substitute spies.
 */
public record ModuleSupport(
    @NonNull ModuleListeners listeners,
    @NonNull ModuleCloseables closeables,
    @NonNull ModuleServices services,
    @NonNull ModuleMenus menus) {

  public static ModuleSupport create() {
    return new ModuleSupport(
        new ModuleListeners(), new ModuleCloseables(), new ModuleServices(), new ModuleMenus());
  }
}
