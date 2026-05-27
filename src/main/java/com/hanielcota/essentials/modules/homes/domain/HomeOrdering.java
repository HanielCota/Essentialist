package com.hanielcota.essentials.modules.homes.domain;

/** Ordering applied to the home list in /homes (within each pinned/unpinned group). */
public enum HomeOrdering {
  NAME,
  MOST_USED,
  RECENT;

  public HomeOrdering next() {
    return switch (this) {
      case NAME -> MOST_USED;
      case MOST_USED -> RECENT;
      case RECENT -> NAME;
    };
  }
}
