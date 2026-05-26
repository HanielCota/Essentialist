package com.hanielcota.essentials.modules.tpa.domain;

/** Ordering applied to the favorites list in {@code TpaFavoritesMenu}. */
public enum FavoriteOrdering {
  NAME,
  RECENT,
  ONLINE_FIRST;

  public FavoriteOrdering next() {
    return switch (this) {
      case NAME -> RECENT;
      case RECENT -> ONLINE_FIRST;
      case ONLINE_FIRST -> NAME;
    };
  }
}
