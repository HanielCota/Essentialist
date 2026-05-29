package com.hanielcota.essentials.modules.info.config;

import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Templates for every entry shown under the "Player" tab. The head entry uses the target's player
 * skin (its {@code icon} is informational only — actual material is {@code PLAYER_HEAD}).
 */
@ConfigSerializable
public record PlayerEntriesSection(
    @Comment("Player head entry. Placeholder: {player}.") InfoEntryConfig head,
    @Comment("Health entry. Placeholder: {health} (rounded integer).") InfoEntryConfig health,
    @Comment("Food entry. Placeholder: {food} (0-20).") InfoEntryConfig food,
    @Comment("XP level entry. Placeholder: {level}.") InfoEntryConfig level,
    @Comment("Game mode entry. Placeholder: {mode} (translated label).") InfoEntryConfig mode,
    @Comment("World entry. Placeholder: {world}.") InfoEntryConfig world,
    @Comment("Location entry. Placeholders: {x}, {y}, {z}, {coords}.") InfoEntryConfig location,
    @Comment("Ping entry. Placeholder: {ping} (milliseconds).") InfoEntryConfig ping,
    @Comment("Session-time entry. Placeholder: {duration} (or {noSession}).")
        InfoEntryConfig session,
    @Comment("Label substituted into {duration} when the player has no recorded session.")
        String noSessionLabel) {

  public static PlayerEntriesSection defaults() {
    return new PlayerEntriesSection(
        InfoEntryConfig.of(Material.PLAYER_HEAD, "<yellow>{player}", "<gray>Player information."),
        InfoEntryConfig.of(Material.GOLDEN_APPLE, "<yellow>Health", "<gray>{health} <red>❤"),
        InfoEntryConfig.of(
            Material.COOKED_BEEF, "<yellow>Food", "<gray>{food} <dark_gray>/ <gray>20"),
        InfoEntryConfig.of(Material.EXPERIENCE_BOTTLE, "<yellow>Level", "<gray>{level}"),
        InfoEntryConfig.of(Material.GRASS_BLOCK, "<yellow>Game mode", "<gray>{mode}"),
        InfoEntryConfig.of(Material.MAP, "<yellow>World", "<gray>{world}"),
        InfoEntryConfig.of(Material.COMPASS, "<yellow>Location", "<gray>{coords}"),
        InfoEntryConfig.of(Material.FEATHER, "<yellow>Ping", "<gray>{ping} ms"),
        InfoEntryConfig.of(Material.CLOCK, "<yellow>Session time", "<gray>{duration}"),
        "just now");
  }
}
