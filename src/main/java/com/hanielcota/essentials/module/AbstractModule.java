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
import lombok.NonNull;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class AbstractModule implements Module {

  private static final Log LOG = Log.of(AbstractModule.class);

  private final ModuleMetadata metadata;
  private final List<Listener> listeners = new ArrayList<>();
  private final List<AutoCloseable> closeable = new ArrayList<>();
  private final List<Class<?>> ownedServices = new ArrayList<>();
  private ModuleContext context;

  protected AbstractModule(@NonNull ModuleMetadata metadata) {
    this.metadata = metadata;
  }

  protected AbstractModule(@NonNull String id) {
    this(ModuleMetadata.minimal(id));
  }

  @Override
  public final ModuleMetadata metadata() {
    return metadata;
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
      for (var listener : listeners) {
        if (listener == null) {
          continue;
        }
        HandlerList.unregisterAll(listener);
      }
      listeners.clear();

      var moduleId = id();
      for (var closeable : closeable) {
        if (closeable == null) {
          continue;
        }
        try {
          closeable.close();
        } catch (Exception e) {
          LOG.warn(e, "Closeable threw during disable of {}", moduleId);
        }
      }
      closeable.clear();

      if (context != null) {
        var services = context.services();
        for (var type : ownedServices) {
          if (type == null) {
            continue;
          }
          services.unregister(type);
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
      var moduleId = id();
      throw new IllegalStateException("Module not enabled: " + moduleId);
    }
    return context;
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

  /** Loads a config, registers the service, and returns the config handle in one call. */
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

  protected final void registerCommand(@NonNull Object handler) {
    var framework = service(PaperCommandFramework.class);
    framework.registerAnnotated(handler);
  }

  protected final void registerMenu(@NonNull Menu menu) {
    var menus = service(MenuService.class);
    menus.register(menu);

    var menuId = menu.id();
    registerCloseable(() -> menus.unregisterDefinition(menuId));
  }

  protected final void registerListener(@NonNull Listener listener) {
    var currentPlugin = plugin();
    var server = currentPlugin.getServer();
    var pluginManager = server.getPluginManager();

    pluginManager.registerEvents(listener, currentPlugin);
    listeners.add(listener);
  }

  protected final void registerCloseable(@NonNull AutoCloseable closeable) {
    this.closeable.add(closeable);
  }

  protected final <T> void registerService(@NonNull Class<T> type, @NonNull T instance) {
    var activeContext = context();
    var services = activeContext.services();

    services.unregister(type);
    services.register(type, instance);
    ownedServices.add(type);

    var commandFrameworkOpt = services.find(PaperCommandFramework.class);
    commandFrameworkOpt.ifPresent(framework -> framework.registerDependency(type, instance));
  }
}
