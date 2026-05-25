package com.hanielcota.essentials.modules.tpa.domain;

import java.time.Duration;
import java.time.Instant;
import lombok.NonNull;

/**
 * An active teleport request held in memory while pending.
 *
 * <p>Identity is the typed {@link RequestId}; each {@link Participant} carries a UUID and a name
 * snapshot, so messaging never has to resolve a possibly offline player. Nothing here is a live
 * Bukkit object, so a request survives reconnects and is safe to move across a proxy network.
 */
public record TeleportRequest(
    RequestId id,
    Participant requester,
    Participant target,
    TeleportRequestType type,
    RequestWindow window) {

  /** Opens a fresh request between two participants, lasting {@code lifetime}. */
  public static TeleportRequest open(
      @NonNull Participant requester,
      @NonNull Participant target,
      @NonNull TeleportRequestType type,
      @NonNull Duration lifetime) {
    return new TeleportRequest(
        RequestId.random(), requester, target, type, RequestWindow.startingNow(lifetime));
  }

  /** Whether this request has lapsed by {@code now}. */
  public boolean isExpired(@NonNull Instant now) {
    return this.window.hasExpired(now);
  }
}
