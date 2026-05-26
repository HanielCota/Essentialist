package com.hanielcota.essentials.modules.tpa.domain;

import java.time.Duration;
import java.util.Map;
import lombok.NonNull;

public record TpaProfile(
    @NonNull TpaPrivacySettings privacy,
    @NonNull TpaStats stats,
    @NonNull TpaPreferences preferences) {

  public TpaProfile(
      @NonNull Map<TeleportRequestType, Boolean> receiveByType,
      long sentRequests,
      long receivedRequests,
      long acceptedSent,
      long acceptCount,
      long totalAcceptLatencyMs,
      boolean autoAcceptFavorites,
      boolean soundsEnabled,
      boolean allowCrossWorld,
      boolean notifyWhenFavorited,
      long dndUntilEpochMs,
      @NonNull FavoriteOrdering favoriteOrdering) {
    this(
        new TpaPrivacySettings(receiveByType, allowCrossWorld, dndUntilEpochMs),
        new TpaStats(
            sentRequests, receivedRequests, acceptedSent, acceptCount, totalAcceptLatencyMs),
        new TpaPreferences(
            autoAcceptFavorites, soundsEnabled, notifyWhenFavorited, favoriteOrdering));
  }

  public static TpaProfile defaults() {
    return new TpaProfile(
        TpaPrivacySettings.defaults(), TpaStats.empty(), TpaPreferences.defaults());
  }

  public boolean accepts(@NonNull TeleportRequestType type) {
    return this.privacy.accepts(type);
  }

  public boolean isDndActive(long nowEpochMs) {
    return this.privacy.isDndActive(nowEpochMs);
  }

  public TpaProfile toggled(@NonNull TeleportRequestType type) {
    var nextPrivacy = this.privacy.toggled(type);

    return withPrivacy(nextPrivacy);
  }

  public TpaProfile incrementSentRequests() {
    var nextStats = this.stats.incrementSentRequests();

    return withStats(nextStats);
  }

  public TpaProfile incrementReceivedRequests() {
    var nextStats = this.stats.incrementReceivedRequests();

    return withStats(nextStats);
  }

  public TpaProfile recordAcceptedOutgoing(@NonNull Duration latency) {
    var nextStats = this.stats.recordAcceptedOutgoing(latency);

    return withStats(nextStats);
  }

  public TpaProfile toggledAutoAcceptFavorites() {
    var nextPreferences = this.preferences.toggledAutoAcceptFavorites();

    return withPreferences(nextPreferences);
  }

  public TpaProfile toggledSounds() {
    var nextPreferences = this.preferences.toggledSounds();

    return withPreferences(nextPreferences);
  }

  public TpaProfile toggledAllowCrossWorld() {
    var nextPrivacy = this.privacy.toggledAllowCrossWorld();

    return withPrivacy(nextPrivacy);
  }

  public TpaProfile toggledNotifyWhenFavorited() {
    var nextPreferences = this.preferences.toggledNotifyWhenFavorited();

    return withPreferences(nextPreferences);
  }

  public TpaProfile withDndUntil(long epochMs) {
    var nextPrivacy = this.privacy.withDndUntil(epochMs);

    return withPrivacy(nextPrivacy);
  }

  public TpaProfile withFavoriteOrdering(@NonNull FavoriteOrdering ordering) {
    var nextPreferences = this.preferences.withFavoriteOrdering(ordering);

    return withPreferences(nextPreferences);
  }

  public Map<TeleportRequestType, Boolean> receiveByType() {
    return this.privacy.receiveByType();
  }

  public long sentRequests() {
    return this.stats.sentRequests();
  }

  public long receivedRequests() {
    return this.stats.receivedRequests();
  }

  public long acceptedSent() {
    return this.stats.acceptedSent();
  }

  public long acceptCount() {
    return this.stats.acceptCount();
  }

  public long totalAcceptLatencyMs() {
    return this.stats.totalAcceptLatencyMs();
  }

  public boolean autoAcceptFavorites() {
    return this.preferences.autoAcceptFavorites();
  }

  public boolean soundsEnabled() {
    return this.preferences.soundsEnabled();
  }

  public boolean allowCrossWorld() {
    return this.privacy.allowCrossWorld();
  }

  public boolean notifyWhenFavorited() {
    return this.preferences.notifyWhenFavorited();
  }

  public long dndUntilEpochMs() {
    return this.privacy.dndUntilEpochMs();
  }

  public FavoriteOrdering favoriteOrdering() {
    return this.preferences.favoriteOrdering();
  }

  private TpaProfile withPrivacy(@NonNull TpaPrivacySettings value) {
    return new TpaProfile(value, this.stats, this.preferences);
  }

  private TpaProfile withStats(@NonNull TpaStats value) {
    return new TpaProfile(this.privacy, value, this.preferences);
  }

  private TpaProfile withPreferences(@NonNull TpaPreferences value) {
    return new TpaProfile(this.privacy, this.stats, value);
  }
}
