package com.hanielcota.essentials.module;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
final class DefaultModuleRegistrar implements ModuleRegistrar {

  private final ModuleContext context;
  private final ModuleEnvironment env;
  private final ModuleListeners listeners;
  private final ModuleCloseables closeables;
  private final ModuleServices services;
  private final ModuleMenus menus;

  @Override
  public void listener(@NonNull Listener listener) {
    var plugin = this.context.plugin();
    this.listeners.register(plugin, listener);
  }

  @Override
  public void command(@NonNull Object handler) {
    var framework = this.env.service(PaperCommandFramework.class);
    framework.registerAnnotated(handler);
  }

  @Override
  public void menu(@NonNull EssentialsMenu menu) {
    var menuService = this.env.service(MenuService.class);
    this.menus.register(menuService, menu, this.closeables);
  }

  @Override
  public void closeable(@NonNull AutoCloseable closeable) {
    this.closeables.register(closeable);
  }

  @Override
  public <T> void provide(@NonNull Class<T> type, @NonNull T instance) {
    this.services.register(this.context, type, instance);
  }

  @Override
  public <C, S> ConfigHandle<C> configure(
      @NonNull String name,
      @NonNull Class<C> configType,
      @NonNull Supplier<C> defaults,
      @NonNull S service) {
    var handle = this.env.config(name, configType, defaults);

    @SuppressWarnings("unchecked")
    var serviceType = (Class<S>) service.getClass();
    provide(serviceType, service);

    return handle;
  }

  @Override
  public <C, S> ConfigHandle<C> configure(
      @NonNull String name,
      @NonNull Class<C> configType,
      @NonNull Supplier<C> defaults,
      @NonNull Class<S> serviceType,
      @NonNull S service) {
    var handle = this.env.config(name, configType, defaults);
    provide(serviceType, service);
    return handle;
  }
}
