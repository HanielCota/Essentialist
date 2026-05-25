package com.hanielcota.essentials.modules.mute.model;

import java.time.Duration;
import java.time.Instant;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * In-memory mute entry. {@code expiresAt == null} means the mute is permanent (until /unmute or
 * plugin restart). Otherwise the mute holds until {@code expiresAt}.
 */
public record Mute(@Nullable Instant expiresAt) {

  public static Mute permanent() {
    return new Mute(null);
  }

  public static Mute until(@NonNull Instant when) {
    return new Mute(when);
  }

  /**
   * Builds a mute from an optional duration relative to {@code now}. A {@code null} duration yields
   * a permanent mute.
   */
  public static Mute from(@Nullable Duration duration, @NonNull Instant now) {
    if (duration == null) {
      return permanent();
    }

    var expiry = now.plus(duration);

    return until(expiry);
  }

  public boolean isPermanent() {
    return this.expiresAt == null;
  }

  public boolean isExpired(@NonNull Instant now) {
    if (this.expiresAt == null) {
      return false;
    }

    return !now.isBefore(this.expiresAt);
  }
}
