package com.hanielcota.essentials.modules.tpa.domain;

import java.time.Duration;
import java.time.Instant;
import lombok.NonNull;

/** The lifetime of a teleport request — when it opened and the instant it lapses. */
public record RequestWindow(Instant createdAt, Instant expiresAt) {

  /** Opens a window now that lasts for {@code lifetime}. */
  public static RequestWindow startingNow(@NonNull Duration lifetime) {
    var now = Instant.now();
    return new RequestWindow(now, now.plus(lifetime));
  }

  /** Whether {@code now} has reached or passed the expiry instant. */
  public boolean hasExpired(@NonNull Instant now) {
    return !now.isBefore(this.expiresAt);
  }
}
