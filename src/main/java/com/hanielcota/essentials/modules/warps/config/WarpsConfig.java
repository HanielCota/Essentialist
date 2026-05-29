package com.hanielcota.essentials.modules.warps.config;

import com.hanielcota.essentials.modules.warps.domain.Warp;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.NonNull;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Root config of the warps module: the warm-up delay, the default icon, the live-occupancy radius,
 * the per-warp menu settings ({@link WarpMenuEntry}, keyed by warp name) and every chat line
 * ({@link WarpsMessages}).
 */
@ConfigSerializable
public record WarpsConfig(
    @Comment("Warm-up delay in seconds before a warp teleport runs. 0 disables the delay.")
        int teleportDelaySeconds,
    @Comment("Maximum length of a warp name created via /setwarp.") int warpNameMaxLength,
    @Comment("Regex a warp name must fully match. Default allows letters, digits, '_' and '-'.")
        String allowedNamePattern,
    @Comment("Default menu icon for a warp created without an explicit icon (/setwarp <name>).")
        Material defaultIcon,
    @Comment(
            "Radius in blocks used to count how many players are 'at' a warp (same world)."
                + " A player is counted from the moment they warp in until they leave this radius.")
        int occupancyRadius,
    @Comment("Full layout and texts of the warps menu.") WarpsMenuConfig menu,
    @Comment(
            "Per-warp menu settings, keyed by warp name (lowercase): icon, display name, lore and"
                + " the PVP flag. Warps with no entry use sensible defaults. The 'example' entry"
                + " below only shows the shape — copy it under a real warp name.")
        Map<String, WarpMenuEntry> warpSettings,
    WarpsMessages messages) {

  public static WarpsConfig defaults() {
    var sample = Map.of("example", WarpMenuEntry.example());

    return new WarpsConfig(
        3,
        32,
        "[A-Za-z0-9_-]+",
        Material.ENDER_PEARL,
        10,
        WarpsMenuConfig.defaults(),
        sample,
        WarpsMessages.defaults());
  }

  /** Configured warm-up duration, clamped to non-negative. */
  public Duration teleportDelay() {
    return Duration.ofSeconds(Math.max(0, teleportDelaySeconds));
  }

  /** Occupancy radius in blocks, clamped to a sane minimum. */
  public int occupancyRadiusBlocks() {
    return Math.max(1, occupancyRadius);
  }

  /** Effective menu icon: a per-warp configured icon wins, else the warp's own stored icon. */
  public Material iconFor(@NonNull Warp warp) {
    var entry = entry(warp.name());
    if (entry != null && entry.icon() != null) {
      return entry.icon();
    }

    return warp.icon();
  }

  /** Display name shown in the menu: the configured one, else the raw warp name. */
  public String displayNameFor(@NonNull Warp warp) {
    var entry = entry(warp.name());
    if (entry != null && entry.displayName() != null && !entry.displayName().isBlank()) {
      return entry.displayName();
    }

    return warp.name();
  }

  /** Extra description lines configured for the warp, or empty. */
  public List<String> loreFor(@NonNull Warp warp) {
    var entry = entry(warp.name());
    if (entry != null && entry.lore() != null) {
      return entry.lore();
    }

    return List.of();
  }

  /** Whether the warp is flagged PVP-active (menu filter + the entry's lore tag). */
  public boolean isPvp(@NonNull String warpName) {
    var entry = entry(warpName);
    return entry != null && entry.pvp();
  }

  private WarpMenuEntry entry(@NonNull String warpName) {
    var key = warpName.toLowerCase(Locale.ROOT);
    return this.warpSettings.get(key);
  }
}
