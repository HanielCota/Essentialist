package com.hanielcota.essentials.modules.ban.config;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * A clickable ban duration in the options menu. A blank {@code duration} means a permanent ban;
 * otherwise it is parsed with the command framework's {@code TimeParser} (e.g. {@code 1h}, {@code
 * 7d}).
 */
@ConfigSerializable
public record BanDurationOption(
    @Comment("Icon shown in the duration row.") Material icon,
    @Comment("Display name of the button (MiniMessage).") String name,
    @Comment("Duration string (blank = permanent). Examples: 1h, 6h, 1d, 7d, 30d.")
        String duration) {

  public boolean isPermanent() {
    return this.duration == null || this.duration.isBlank();
  }

  public static List<BanDurationOption> defaults() {
    return List.of(
        new BanDurationOption(Material.BEDROCK, "<dark_red>Permanent", ""),
        new BanDurationOption(Material.CLOCK, "<yellow>1 hour", "1h"),
        new BanDurationOption(Material.CLOCK, "<yellow>6 hours", "6h"),
        new BanDurationOption(Material.CLOCK, "<gold>1 day", "1d"),
        new BanDurationOption(Material.CLOCK, "<gold>7 days", "7d"),
        new BanDurationOption(Material.CLOCK, "<red>30 days", "30d"));
  }
}
