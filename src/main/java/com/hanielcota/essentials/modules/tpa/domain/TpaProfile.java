package com.hanielcota.essentials.modules.tpa.domain;

import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;
import lombok.NonNull;

public record TpaProfile(
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

  public static TpaProfile defaults() {
    var receive = new EnumMap<TeleportRequestType, Boolean>(TeleportRequestType.class);
    receive.put(TeleportRequestType.TPA, true);
    receive.put(TeleportRequestType.TPAHERE, true);

    return new TpaProfile(
        Map.copyOf(receive), 0, 0, 0, 0, 0, false, true, true, true, 0, FavoriteOrdering.NAME);
  }

  public boolean accepts(@NonNull TeleportRequestType type) {
    return this.receiveByType.getOrDefault(type, false);
  }

  public boolean isDndActive(long nowEpochMs) {
    return this.dndUntilEpochMs > nowEpochMs;
  }

  public TpaProfile toggled(@NonNull TeleportRequestType type) {
    var next = new EnumMap<>(this.receiveByType);
    var current = next.getOrDefault(type, false);
    next.put(type, !current);

    return copy().receiveByType(Map.copyOf(next)).build();
  }

  public TpaProfile incrementSentRequests() {
    return copy().sentRequests(this.sentRequests + 1).build();
  }

  public TpaProfile incrementReceivedRequests() {
    return copy().receivedRequests(this.receivedRequests + 1).build();
  }

  public TpaProfile recordAcceptedOutgoing(@NonNull Duration latency) {
    var latencyMs = Math.max(0L, latency.toMillis());
    var nextAcceptedSent = this.acceptedSent + 1;
    var nextAcceptCount = this.acceptCount + 1;
    var nextTotalLatency = this.totalAcceptLatencyMs + latencyMs;

    return copy()
        .acceptedSent(nextAcceptedSent)
        .acceptCount(nextAcceptCount)
        .totalAcceptLatencyMs(nextTotalLatency)
        .build();
  }

  public TpaProfile toggledAutoAcceptFavorites() {
    return copy().autoAcceptFavorites(!this.autoAcceptFavorites).build();
  }

  public TpaProfile toggledSounds() {
    return copy().soundsEnabled(!this.soundsEnabled).build();
  }

  public TpaProfile toggledAllowCrossWorld() {
    return copy().allowCrossWorld(!this.allowCrossWorld).build();
  }

  public TpaProfile toggledNotifyWhenFavorited() {
    return copy().notifyWhenFavorited(!this.notifyWhenFavorited).build();
  }

  public TpaProfile withDndUntil(long epochMs) {
    return copy().dndUntilEpochMs(epochMs).build();
  }

  public TpaProfile withFavoriteOrdering(@NonNull FavoriteOrdering ordering) {
    return copy().favoriteOrdering(ordering).build();
  }

  private Builder copy() {
    return new Builder(this);
  }

  private static final class Builder {
    private Map<TeleportRequestType, Boolean> receiveByType;
    private long sentRequests;
    private long receivedRequests;
    private long acceptedSent;
    private long acceptCount;
    private long totalAcceptLatencyMs;
    private boolean autoAcceptFavorites;
    private boolean soundsEnabled;
    private boolean allowCrossWorld;
    private boolean notifyWhenFavorited;
    private long dndUntilEpochMs;
    private FavoriteOrdering favoriteOrdering;

    private Builder(TpaProfile source) {
      this.receiveByType = source.receiveByType;
      this.sentRequests = source.sentRequests;
      this.receivedRequests = source.receivedRequests;
      this.acceptedSent = source.acceptedSent;
      this.acceptCount = source.acceptCount;
      this.totalAcceptLatencyMs = source.totalAcceptLatencyMs;
      this.autoAcceptFavorites = source.autoAcceptFavorites;
      this.soundsEnabled = source.soundsEnabled;
      this.allowCrossWorld = source.allowCrossWorld;
      this.notifyWhenFavorited = source.notifyWhenFavorited;
      this.dndUntilEpochMs = source.dndUntilEpochMs;
      this.favoriteOrdering = source.favoriteOrdering;
    }

    private Builder receiveByType(Map<TeleportRequestType, Boolean> value) {
      this.receiveByType = value;
      return this;
    }

    private Builder sentRequests(long value) {
      this.sentRequests = value;
      return this;
    }

    private Builder receivedRequests(long value) {
      this.receivedRequests = value;
      return this;
    }

    private Builder acceptedSent(long value) {
      this.acceptedSent = value;
      return this;
    }

    private Builder acceptCount(long value) {
      this.acceptCount = value;
      return this;
    }

    private Builder totalAcceptLatencyMs(long value) {
      this.totalAcceptLatencyMs = value;
      return this;
    }

    private Builder autoAcceptFavorites(boolean value) {
      this.autoAcceptFavorites = value;
      return this;
    }

    private Builder soundsEnabled(boolean value) {
      this.soundsEnabled = value;
      return this;
    }

    private Builder allowCrossWorld(boolean value) {
      this.allowCrossWorld = value;
      return this;
    }

    private Builder notifyWhenFavorited(boolean value) {
      this.notifyWhenFavorited = value;
      return this;
    }

    private Builder dndUntilEpochMs(long value) {
      this.dndUntilEpochMs = value;
      return this;
    }

    private Builder favoriteOrdering(FavoriteOrdering value) {
      this.favoriteOrdering = value;
      return this;
    }

    private TpaProfile build() {
      return new TpaProfile(
          this.receiveByType,
          this.sentRequests,
          this.receivedRequests,
          this.acceptedSent,
          this.acceptCount,
          this.totalAcceptLatencyMs,
          this.autoAcceptFavorites,
          this.soundsEnabled,
          this.allowCrossWorld,
          this.notifyWhenFavorited,
          this.dndUntilEpochMs,
          this.favoriteOrdering);
    }
  }
}
