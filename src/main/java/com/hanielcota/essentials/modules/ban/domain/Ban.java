package com.hanielcota.essentials.modules.ban.domain;

import java.time.Duration;
import java.time.Instant;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * In-memory ban entry. {@code expiresAt == null} means the ban is permanent (until /unban).
 * Otherwise it holds until {@code expiresAt}. Mirrors {@code Mute} but carries the moderation
 * metadata a ban needs to render in the ban list and the login-deny screen.
 */
public record Ban(
    @Nullable Instant expiresAt,
    @NonNull String reason,
    @NonNull String issuer,
    @NonNull Instant createdAt) {

  public static Ban permanent(
      @NonNull String reason, @NonNull String issuer, @NonNull Instant now) {
    return new Ban(null, reason, issuer, now);
  }

  public static Ban until(
      @NonNull Instant when, @NonNull String reason, @NonNull String issuer, @NonNull Instant now) {
    return new Ban(when, reason, issuer, now);
  }

  /**
   * Builds a ban from an optional duration relative to {@code now}. A {@code null} duration yields
   * a permanent ban.
   */
  public static Ban from(
      @Nullable Duration duration,
      @NonNull String reason,
      @NonNull String issuer,
      @NonNull Instant now) {
    if (duration == null) {
      return permanent(reason, issuer, now);
    }

    var expiry = now.plus(duration);

    return until(expiry, reason, issuer, now);
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
