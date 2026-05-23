package com.hanielcota.essentials.module;

import com.github.hanielcota.menuframework.api.Menu;
import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.config.ConfigService;
import com.hanielcota.essentials.util.Log;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class AbstractModule implements Module {

  private static final Log LOG = Log.of(AbstractModule.class);

  private final ModuleMetadata metadata;
  private final List<Listener> listeners = new ArrayList<>();
  private final List<AutoCloseable> closeable = new ArrayList<>();
  private final List<Class<?>> ownedServices = new ArrayList<>();
  private ModuleContext context;

  protected AbstractModule(ModuleMetadata metadata) {
    this.metadata = metadata;
  }

  protected AbstractModule(String id) {
    this(ModuleMetadata.minimal(id));
  }

  @Override
  public final ModuleMetadata metadata() {
    return metadata;
  }

  @Override
  public final void enable(ModuleContext context) {
    this.context = context;
    onEnable();
  }

  @Override
  public final void disable() {
    try {
      onDisable();
    } finally {
      for (Listener listener : listeners) {
        HandlerList.unregisterAll(listener);
      }
      listeners.clear();
      for (AutoCloseable closeable : closeable) {
        try {
          closeable.close();
        } catch (Exception e) {
          LOG.warn(e, "Closeable threw during disable of {}", id());
        }
      }
      closeable.clear();
      if (context != null) {
        for (Class<?> type : ownedServices) {
          context.services().unregister(type);
        }
      }
      ownedServices.clear();
      this.context = null;
    }
  }

  protected abstract void onEnable();

  protected void onDisable() {}

  protected final ModuleContext context() {
    if (context == null) {
      throw new IllegalStateException("Module not enabled: " + id());
    }
    return context;
  }

  protected final EssentialsPlugin plugin() {
    return context().plugin();
  }

  protected final <T> T service(Class<T> type) {
    return context().services().resolve(type);
  }

  protected final <T> ConfigHandle<T> config(String name, Class<T> type, Supplier<T> defaults) {
    return service(ConfigService.class).load(name, type, defaults);
  }

  /** Loads a config, registers the service, and returns the config handle in one call. */
  protected final <C, S> ConfigHandle<C> configure(
      String name, Class<C> configType, Supplier<C> defaults, S service) {
    var handle = config(name, configType, defaults);
    @SuppressWarnings("unchecked")
    Class<S> serviceType = (Class<S>) service.getClass();
    registerService(serviceType, service);
    return handle;
  }

  protected final void registerCommand(Object handler) {
    service(PaperCommandFramework.class).registerAnnotated(handler);
  }

  protected final void registerMenu(Menu menu) {
    MenuService menus = service(MenuService.class);
    menus.register(menu);
    registerCloseable(() -> menus.unregisterDefinition(menu.id()));
  }

  protected final void registerListener(Listener listener) {
    plugin().getServer().getPluginManager().registerEvents(listener, plugin());
    listeners.add(listener);
  }

  protected final void registerCloseable(AutoCloseable closeable) {
    this.closeable.add(closeable);
  }

  protected final <T> void registerService(Class<T> type, T instance) {
    var services = context().services();
    services.unregister(type);
    services.register(type, instance);
    ownedServices.add(type);
    services
        .find(PaperCommandFramework.class)
        .ifPresent(framework -> framework.registerDependency(type, instance));
  }
}
