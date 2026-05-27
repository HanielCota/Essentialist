package com.hanielcota.essentials.modules.info.config;

import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Templates for every entry shown under the "Jogador" tab. The head entry uses the target's player
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
        InfoEntryConfig.of(
            Material.PLAYER_HEAD, "<yellow>{player}", "<gray>Informações do jogador."),
        InfoEntryConfig.of(Material.GOLDEN_APPLE, "<yellow>Vida", "<gray>{health} <red>❤"),
        InfoEntryConfig.of(
            Material.COOKED_BEEF, "<yellow>Fome", "<gray>{food} <dark_gray>/ <gray>20"),
        InfoEntryConfig.of(Material.EXPERIENCE_BOTTLE, "<yellow>Nível", "<gray>{level}"),
        InfoEntryConfig.of(Material.GRASS_BLOCK, "<yellow>Modo de jogo", "<gray>{mode}"),
        InfoEntryConfig.of(Material.MAP, "<yellow>Mundo", "<gray>{world}"),
        InfoEntryConfig.of(Material.COMPASS, "<yellow>Localização", "<gray>{coords}"),
        InfoEntryConfig.of(Material.FEATHER, "<yellow>Ping", "<gray>{ping} ms"),
        InfoEntryConfig.of(Material.CLOCK, "<yellow>Tempo de sessão", "<gray>{duration}"),
        "agora mesmo");
  }
}
