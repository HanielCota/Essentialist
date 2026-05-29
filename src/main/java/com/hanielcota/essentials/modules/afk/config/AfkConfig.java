package com.hanielcota.essentials.modules.afk.config;

import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record AfkConfig(
    @Comment(
            "Seconds of inactivity before the auto checker marks a player AFK. Set <= 0 to"
                + " disable.")
        int idleThresholdSeconds,
    @Comment("How often (seconds) the auto checker sweeps online players for idleness. Minimum 1.")
        int checkIntervalSeconds,
    @Comment(
            "Per-player movement debounce in milliseconds before movement counts as activity. Keeps"
                + " the move event off the hot path.")
        int moveDebounceMillis,
    @Comment("Broadcast when a player goes AFK without a reason. Placeholder: {player}.")
        String enterFormat,
    @Comment("Broadcast when a player goes AFK with a reason. Placeholders: {player}, {reason}.")
        String enterWithReasonFormat,
    @Comment("Broadcast when a player returns from AFK. Placeholder: {player}.")
        String exitFormat) {

  public static AfkConfig defaults() {
    return new AfkConfig(
        300,
        5,
        250,
        "<gray><gold>{player}</gold> is now <yellow>AFK</yellow>.",
        "<gray><gold>{player}</gold> is now <yellow>AFK</yellow><gray>: <white>{reason}",
        "<gray><gold>{player}</gold> is no longer <yellow>AFK</yellow><gray>.");
  }

  public String formatEnter(@NonNull String player) {
    return enterFormat.replace("{player}", player);
  }

  public String formatEnterWithReason(@NonNull String player, @NonNull String reason) {
    var withPlayer = enterWithReasonFormat.replace("{player}", player);

    return withPlayer.replace("{reason}", reason);
  }

  public String formatExit(@NonNull String player) {
    return exitFormat.replace("{player}", player);
  }
}
