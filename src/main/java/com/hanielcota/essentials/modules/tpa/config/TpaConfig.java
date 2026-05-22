package com.hanielcota.essentials.modules.tpa.config;

import java.time.Duration;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Root config of the {@code /tpa} module: the request lifetime plus two sections — {@link
 * TpaMessages} (every chat line) and {@link TpaMenuConfig} (the {@code /tpahistory} inventory).
 */
@ConfigSerializable
public record TpaConfig(
    @Comment("How long a pending request lives before expiring, in seconds (minimum 5).")
        int requestExpirySeconds,
    TpaMessages messages,
    TpaMenuConfig menu) {

  public static TpaConfig defaults() {
    return new TpaConfig(60, TpaMessages.defaults(), TpaMenuConfig.defaults());
  }

  /** Configured request lifetime, clamped to a sane minimum. */
  public Duration requestExpiry() {
    return Duration.ofSeconds(Math.max(5, requestExpirySeconds));
  }
}
