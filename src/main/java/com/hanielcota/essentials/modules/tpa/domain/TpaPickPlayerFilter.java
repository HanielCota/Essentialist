package com.hanielcota.essentials.modules.tpa.domain;

/** Filter applied to the candidate list in {@code TpaPickPlayerMenu}. */
public enum TpaPickPlayerFilter {
  ALL,
  FAVORITES,
  SAME_WORLD,
  RECENT;

  public TpaPickPlayerFilter next() {
    return switch (this) {
      case ALL -> FAVORITES;
      case FAVORITES -> SAME_WORLD;
      case SAME_WORLD -> RECENT;
      case RECENT -> ALL;
    };
  }
}
