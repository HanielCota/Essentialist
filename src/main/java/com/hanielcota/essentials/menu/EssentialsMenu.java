package com.hanielcota.essentials.menu;

import com.github.hanielcota.menuframework.api.MenuService;
import lombok.NonNull;

/**
 * A module-owned menu. Implementations are handed to {@code ModuleRegistrar.menu(...)} during
 * enable; the registrar calls {@link #register(MenuService)} to publish the menu and unregisters it
 * by {@link #id()} on module disable.
 */
public interface EssentialsMenu {

  /** Stable identifier the menu is registered and later unregistered under. */
  @NonNull String id();

  /** Publishes this menu with the framework's {@link MenuService}. Called once, during enable. */
  void register(@NonNull MenuService menus);
}
