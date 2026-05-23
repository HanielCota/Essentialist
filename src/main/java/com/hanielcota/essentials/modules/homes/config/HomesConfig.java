package com.hanielcota.essentials.modules.homes.config;

import java.time.Duration;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Root config of the homes module: the warm-up delay, the default name used by {@code /sethome} and
 * {@code /home} with no argument, the fallback per-player limit, and every chat line ({@link
 * HomesMessages}).
 */
@ConfigSerializable
public record HomesConfig(
    @Comment("Warm-up delay in seconds before /home teleports. 0 disables the delay.")
        int teleportDelaySeconds,
    @Comment("Home name used when /sethome and /home are run with no argument.")
        String defaultHomeName,
    @Comment(
            "Default home limit per player when they have no essentials.home.limit.N permission. "
                + "Set to 0 to require permissions for every home.")
        int defaultLimit,
    HomesMessages messages) {

  public static HomesConfig defaults() {
    return new HomesConfig(3, "home", 1, HomesMessages.defaults());
  }

  /** Configured warm-up duration, clamped to non-negative. */
  public Duration teleportDelay() {
    return Duration.ofSeconds(Math.max(0, teleportDelaySeconds));
  }
}
