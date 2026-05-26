package com.hanielcota.essentials.modules.tpa.domain;

import lombok.NonNull;

public record TpaPreferences(
    boolean autoAcceptFavorites,
    boolean soundsEnabled,
    boolean notifyWhenFavorited,
    @NonNull FavoriteOrdering favoriteOrdering) {

  public static TpaPreferences defaults() {
    return new TpaPreferences(false, true, true, FavoriteOrdering.NAME);
  }

  public TpaPreferences toggledAutoAcceptFavorites() {
    return new TpaPreferences(
        !this.autoAcceptFavorites,
        this.soundsEnabled,
        this.notifyWhenFavorited,
        this.favoriteOrdering);
  }

  public TpaPreferences toggledSounds() {
    return new TpaPreferences(
        this.autoAcceptFavorites,
        !this.soundsEnabled,
        this.notifyWhenFavorited,
        this.favoriteOrdering);
  }

  public TpaPreferences toggledNotifyWhenFavorited() {
    return new TpaPreferences(
        this.autoAcceptFavorites,
        this.soundsEnabled,
        !this.notifyWhenFavorited,
        this.favoriteOrdering);
  }

  public TpaPreferences withFavoriteOrdering(@NonNull FavoriteOrdering ordering) {
    return new TpaPreferences(
        this.autoAcceptFavorites, this.soundsEnabled, this.notifyWhenFavorited, ordering);
  }
}
