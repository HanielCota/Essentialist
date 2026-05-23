package com.hanielcota.essentials.modules.spawn.config;

import java.time.Duration;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Root config of the {@code spawn} module: the warm-up delay and every chat line ({@link
 * SpawnMessages}).
 */
@ConfigSerializable
public record SpawnConfig(
    @Comment("Warm-up delay in seconds before /spawn teleports. 0 disables the delay.")
        int teleportDelaySeconds,
    SpawnMessages messages) {

  public static SpawnConfig defaults() {
    return new SpawnConfig(3, SpawnMessages.defaults());
  }

  /** Configured warm-up duration, clamped to non-negative. */
  public Duration teleportDelay() {
    return Duration.ofSeconds(Math.max(0, teleportDelaySeconds));
  }
}
