package com.hanielcota.essentials.module;

import com.hanielcota.essentials.module.environment.DefaultModuleEnvironment;
import com.hanielcota.essentials.module.environment.ModuleContext;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.environment.ModuleServices;
import com.hanielcota.essentials.module.lifecycle.ModuleCloseables;
import com.hanielcota.essentials.module.lifecycle.ModuleListeners;
import com.hanielcota.essentials.module.lifecycle.ModuleMenus;
import com.hanielcota.essentials.module.registration.DefaultModuleRegistrar;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.shared.Log;
import lombok.NonNull;

/**
 * Base for every feature module. The single hook subclasses implement is {@link
 * #onEnable(ModuleEnvironment, ModuleRegistrar)} — the environment exposes services and configs,
 * the registrar publishes listeners / commands / menus / services. Both go out of scope as soon as
 * enable returns, which keeps the lifecycle obvious: modules do not have a long-lived handle to the
 * plugin or registry.
 *
 * <p>The {@link #disable()} hook tears down everything the registrar collected, in registration
 * order, then runs the subclass's {@link #onDisable()} (which usually has nothing to clean up).
 */
public abstract class AbstractModule implements Module {

  private static final Log LOG = Log.of(AbstractModule.class);

  private final ModuleMetadata metadata;
  private final ModuleListeners listeners = new ModuleListeners();
  private final ModuleCloseables closeables = new ModuleCloseables();
  private final ModuleServices services = new ModuleServices();
  private final ModuleMenus menus = new ModuleMenus();
  private ModuleContext context;

  protected AbstractModule(@NonNull ModuleMetadata metadata) {
    this.metadata = metadata;
  }

  protected AbstractModule(@NonNull String id) {
    this(ModuleMetadata.minimal(id));
  }

  @Override
  public final ModuleMetadata metadata() {
    return this.metadata;
  }

  @Override
  public final void enable(@NonNull ModuleContext context) {
    this.context = context;

    var env = createEnvironment(context);
    var registrar =
        createRegistrar(context, env, this.listeners, this.closeables, this.services, this.menus);

    onEnable(env, registrar);
  }

  protected ModuleEnvironment createEnvironment(@NonNull ModuleContext context) {
    return new DefaultModuleEnvironment(context);
  }

  protected ModuleRegistrar createRegistrar(
      @NonNull ModuleContext context,
      @NonNull ModuleEnvironment env,
      @NonNull ModuleListeners listeners,
      @NonNull ModuleCloseables closeables,
      @NonNull ModuleServices services,
      @NonNull ModuleMenus menus) {
    return new DefaultModuleRegistrar(context, env, listeners, closeables, services, menus);
  }

  @Override
  public final void disable() {
    try {
      onDisable();
    } finally {
      var moduleId = id();
      this.listeners.unregisterAll();
      this.closeables.closeAll(moduleId, LOG);
      this.services.unregisterOwned(this.context);
      this.context = null;
    }
  }

  /**
   * Wire collaborators here. {@code env} reads services / configs / the plugin; {@code registrar}
   * publishes listeners, commands, menus, services and closeables. Neither reference should be
   * stored as a field — the registrar's writes are only valid during this call, and the env is
   * cheap to receive as a parameter when a private helper needs it.
   */
  protected abstract void onEnable(
      @NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar);

  /**
   * Optional teardown hook. Most modules leave this empty — the registrar's closeables already
   * cover listeners, services, menus and any explicit {@code closeable(...)} registrations.
   */
  protected void onDisable() {}
}
