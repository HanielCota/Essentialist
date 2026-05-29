package com.hanielcota.essentials.modules.essentials.menu;

import lombok.NonNull;

/**
 * Groups modules in the admin menu. Selected via the cycling filter button; display labels live in
 * {@code ModulesFilterConfig} so they stay admin-configurable.
 */
public enum ModuleCategory {
  PROTECTION,
  TELEPORT,
  CHAT,
  ITEMS,
  PLAYER,
  ADMIN,
  OTHER;

  /** The next category in the cycle, wrapping back to the first. */
  public @NonNull ModuleCategory next() {
    var all = values();
    var nextOrdinal = (ordinal() + 1) % all.length;

    return all[nextOrdinal];
  }
}
