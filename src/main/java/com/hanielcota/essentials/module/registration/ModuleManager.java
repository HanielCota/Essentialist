package com.hanielcota.essentials.module.registration;

import com.hanielcota.essentials.module.Module;
import com.hanielcota.essentials.module.discovery.ModuleDependencyResolver;
import com.hanielcota.essentials.module.environment.ModuleContext;
import com.hanielcota.essentials.module.lifecycle.ModuleLifecycle;
import com.hanielcota.essentials.module.registry.ModuleRegistry;
import java.util.Collection;
import lombok.NonNull;

/**
 * Facade over the module subsystem. Composes {@link ModuleRegistry} (state holder) and {@link
 * ModuleLifecycle} (enable/disable driver) so callers (bootstrap, core) keep a single dependency.
 *
 * <p>Dependency resolution lives in the stateless {@link ModuleDependencyResolver}.
 */
public final class ModuleManager {

  private final ModuleRegistry registry = new ModuleRegistry();
  private final ModuleLifecycle lifecycle = new ModuleLifecycle(this.registry);

  public void register(@NonNull Module module) {
    this.registry.register(module);
  }

  public void enableAll(@NonNull ModuleContext context) {
    this.lifecycle.enableAll(context);
  }

  public void disableAll() {
    this.lifecycle.disableAll();
  }

  public Collection<Module> all() {
    return this.registry.all();
  }
}
