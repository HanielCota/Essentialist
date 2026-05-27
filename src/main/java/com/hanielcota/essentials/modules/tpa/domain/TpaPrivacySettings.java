package com.hanielcota.essentials.modules.tpa.domain;

import java.util.EnumMap;
import java.util.Map;
import lombok.NonNull;

public record TpaPrivacySettings(
    @NonNull Map<TeleportRequestType, Boolean> receiveByType,
    boolean allowCrossWorld,
    long dndUntilEpochMs) {

  public TpaPrivacySettings {
    receiveByType = Map.copyOf(receiveByType);
  }

  public static TpaPrivacySettings defaults() {
    var receive = new EnumMap<TeleportRequestType, Boolean>(TeleportRequestType.class);
    receive.put(TeleportRequestType.TPA, true);
    receive.put(TeleportRequestType.TPAHERE, true);

    return new TpaPrivacySettings(receive, true, 0);
  }

  public boolean accepts(@NonNull TeleportRequestType type) {
    return this.receiveByType.getOrDefault(type, true);
  }

  public boolean isDndActive(long nowEpochMs) {
    return this.dndUntilEpochMs > nowEpochMs;
  }

  public TpaPrivacySettings toggled(@NonNull TeleportRequestType type) {
    var next = new EnumMap<>(this.receiveByType);
    var current = next.getOrDefault(type, false);
    next.put(type, !current);

    return new TpaPrivacySettings(next, this.allowCrossWorld, this.dndUntilEpochMs);
  }

  public TpaPrivacySettings toggledAllowCrossWorld() {
    return new TpaPrivacySettings(this.receiveByType, !this.allowCrossWorld, this.dndUntilEpochMs);
  }

  public TpaPrivacySettings withDndUntil(long epochMs) {
    return new TpaPrivacySettings(this.receiveByType, this.allowCrossWorld, epochMs);
  }
}
