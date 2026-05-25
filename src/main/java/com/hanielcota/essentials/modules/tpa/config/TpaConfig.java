package com.hanielcota.essentials.modules.tpa.config;

import java.time.Duration;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Root config of the {@code /tpa} module: the request lifetime plus three sections — {@link
 * TpaMessages} (every chat line), {@link TpaMenuConfig} (the {@code /tpahistory} inventory) and
 * {@link TpaHelpMenuConfig} (the {@code /tpa} help menu shown when the command has no target).
 */
@ConfigSerializable
public record TpaConfig(
    @Comment("How long a pending request lives before expiring, in seconds (minimum 5).")
        int requestExpirySeconds,
    TpaMessages messages,
    TpaMenuConfig menu,
    TpaHelpMenuConfig helpMenu) {

  public static TpaConfig defaults() {
    return new TpaConfig(
        60, TpaMessages.defaults(), TpaMenuConfig.defaults(), TpaHelpMenuConfig.defaults());
  }

  /** Configured request lifetime, clamped to a sane minimum. */
  public Duration requestExpiry() {
    return Duration.ofSeconds(Math.max(5, requestExpirySeconds));
  }
}
