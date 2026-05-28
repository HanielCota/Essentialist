package com.hanielcota.essentials.modules.warps.config;

import java.time.Duration;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Root config of the warps module: the warm-up delay and every chat line ({@link WarpsMessages}).
 */
@ConfigSerializable
public record WarpsConfig(
    @Comment("Warm-up delay in seconds before /warp teleports. 0 disables the delay.")
        int teleportDelaySeconds,
    @Comment("Maximum length of a warp name created via /setwarp.") int warpNameMaxLength,
    @Comment("Regex a warp name must fully match. Default allows letters, digits, '_' and '-'.")
        String allowedNamePattern,
    WarpsMessages messages) {

  public static WarpsConfig defaults() {
    return new WarpsConfig(3, 32, "[A-Za-z0-9_-]+", WarpsMessages.defaults());
  }

  /** Configured warm-up duration, clamped to non-negative. */
  public Duration teleportDelay() {
    return Duration.ofSeconds(Math.max(0, teleportDelaySeconds));
  }
}
