package com.hanielcota.essentials.modules.warps.menu;

import lombok.NonNull;

/**
 * Ordering/filter applied to the warps menu, cycled by the filter button. Display labels live in
 * {@code WarpsFilterConfig} so they stay admin-configurable.
 */
public enum WarpFilter {
  DEFAULT,
  MOST_PLAYERS,
  LEAST_PLAYERS,
  MOST_LIKED,
  FAVORITES,
  PVP;

  /** The next filter in the cycle, wrapping back to the first. */
  public @NonNull WarpFilter next() {
    var all = values();
    var nextOrdinal = (ordinal() + 1) % all.length;
    return all[nextOrdinal];
  }
}
