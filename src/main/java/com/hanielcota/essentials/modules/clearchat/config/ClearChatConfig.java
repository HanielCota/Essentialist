package com.hanielcota.essentials.modules.clearchat.config;

import java.util.Objects;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record ClearChatConfig(
    @Comment("Number of blank lines used to flush the chat (clamped to 0-300).") int lines,
    @Comment("Broadcast after the chat is cleared. Placeholder: {player}.") String announcement) {

  public static ClearChatConfig defaults() {
    return new ClearChatConfig(100, "<yellow>O chat foi limpo por <gold>{player}</gold>.");
  }

  /** Configured line count clamped to a safe range. */
  public int effectiveLines() {
    return Math.clamp(lines, 0, 300);
  }

  public String formatAnnouncement(String player) {
    Objects.requireNonNull(player, "player");
    return announcement.replace("{player}", player);
  }
}
