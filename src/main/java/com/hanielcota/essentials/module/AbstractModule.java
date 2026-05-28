package com.hanielcota.essentials.module;

import com.hanielcota.essentials.module.environment.DefaultModuleEnvironment;
import com.hanielcota.essentials.module.environment.ModuleContext;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
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
  private final ModuleSupport support;
  private ModuleContext context;

  protected AbstractModule(@NonNull ModuleMetadata metadata) {
    this(metadata, ModuleSupport.create());
  }

  protected AbstractModule(@NonNull String id) {
    this(ModuleMetadata.minimal(id));
  }

  protected AbstractModule(@NonNull ModuleMetadata metadata, @NonNull ModuleSupport support) {
    this.metadata = metadata;
    this.support = support;
  }

  @Override
  public final ModuleMetadata metadata() {
    return this.metadata;
  }

  @Override
  public final void enable(@NonNull ModuleContext context) {
    this.context = context;

    var env = createEnvironment(context);
    var registrar = createRegistrar(context, env);

    onEnable(env, registrar);
  }

  protected ModuleEnvironment createEnvironment(@NonNull ModuleContext context) {
    return new DefaultModuleEnvironment(context);
  }

  protected ModuleRegistrar createRegistrar(
      @NonNull ModuleContext context, @NonNull ModuleEnvironment env) {
    return new DefaultModuleRegistrar(
        context,
        env,
        this.support.listeners(),
        this.support.closeables(),
        this.support.services(),
        this.support.menus());
  }

  @Override
  public final void disable() {
    try {
      onDisable();
    } finally {
      var moduleId = id();
      this.support.listeners().unregisterAll();
      this.support.closeables().closeAll(moduleId, LOG);
      this.support.services().unregisterOwned(this.context);
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
