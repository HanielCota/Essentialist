package com.hanielcota.essentials.module;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.config.ConfigService;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.util.Log;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.function.Supplier;
import lombok.NonNull;
import org.bukkit.event.Listener;

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
    onEnable();
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

  protected abstract void onEnable();

  protected void onDisable() {}

  protected final ModuleContext context() {
    if (this.context == null) {
      var moduleId = id();
      throw new IllegalStateException("Module not enabled: " + moduleId);
    }
    return this.context;
  }

  protected final EssentialsPlugin plugin() {
    var activeContext = context();
    return activeContext.plugin();
  }

  protected final <T> T service(@NonNull Class<T> type) {
    var activeContext = context();
    var services = activeContext.services();

    return services.resolve(type);
  }

  protected final <T> ConfigHandle<T> config(
      @NonNull String name, @NonNull Class<T> type, @NonNull Supplier<T> defaults) {
    var configService = service(ConfigService.class);
    return configService.load(name, type, defaults);
  }

  /**
   * Loads a config, registers the service under its concrete runtime class, and returns the config
   * handle in one call. Convenience overload for the common case where the service has no interface
   * and callers will look it up as {@code service(ConcreteService.class)}. If the service needs to
   * be registered under an interface or supertype, prefer {@link #configure(String, Class,
   * Supplier, Class, Object)}.
   */
  protected final <C, S> ConfigHandle<C> configure(
      @NonNull String name,
      @NonNull Class<C> configType,
      @NonNull Supplier<C> defaults,
      @NonNull S service) {
    var handle = config(name, configType, defaults);

    @SuppressWarnings("unchecked")
    var serviceType = (Class<S>) service.getClass();
    registerService(serviceType, service);

    return handle;
  }

  /**
   * Loads a config and registers the service under the explicit {@code serviceType}. Use this when
   * the service should be looked up via an interface or supertype rather than its concrete class.
   */
  protected final <C, S> ConfigHandle<C> configure(
      @NonNull String name,
      @NonNull Class<C> configType,
      @NonNull Supplier<C> defaults,
      @NonNull Class<S> serviceType,
      @NonNull S service) {
    var handle = config(name, configType, defaults);
    registerService(serviceType, service);
    return handle;
  }

  protected final void registerCommand(@NonNull Object handler) {
    var framework = service(PaperCommandFramework.class);
    framework.registerAnnotated(handler);
  }

  protected final void registerMenu(@NonNull EssentialsMenu menu) {
    var menuService = service(MenuService.class);
    this.menus.register(menuService, menu, this.closeables);
  }

  protected final void registerListener(@NonNull Listener listener) {
    var currentPlugin = plugin();
    this.listeners.register(currentPlugin, listener);
  }

  protected final void registerCloseable(@NonNull AutoCloseable closeable) {
    this.closeables.register(closeable);
  }

  protected final <T> void registerService(@NonNull Class<T> type, @NonNull T instance) {
    var activeContext = context();
    this.services.register(activeContext, type, instance);
  }
}
