package com.hanielcota.essentials.modules.kick.config;

import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record KickConfig(
    @Comment("Reason used when /kick is run without one.") String defaultReason,
    @Comment("Kick screen shown to the kicked player. Placeholder: {reason}.") String screen,
    @Comment("Confirmation shown to the sender. Placeholders: {player}, {reason}.") String kicked) {

  public static KickConfig defaults() {
    return new KickConfig(
        "Você foi expulso do servidor.",
        "<red>{reason}",
        "<green>Você expulsou <gold>{player}</gold>.");
  }

  /** Returns {@code provided} when it has content, otherwise the configured default reason. */
  public String reasonOr(@NonNull String provided) {
    return provided.isBlank() ? defaultReason : provided;
  }

  public String formatScreen(@NonNull String reason) {
    return screen.replace("{reason}", reason);
  }

  public String formatKicked(@NonNull String player, @NonNull String reason) {
    return kicked.replace("{player}", player).replace("{reason}", reason);
  }
}
