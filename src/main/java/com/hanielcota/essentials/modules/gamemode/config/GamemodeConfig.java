package com.hanielcota.essentials.modules.gamemode.config;

import com.hanielcota.essentials.config.MessagePair;
import java.util.Locale;
import java.util.Map;
import org.bukkit.GameMode;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record GamemodeConfig(
    @Comment("Placeholders: {gamemode}.") String updated,
    @Comment("Placeholders: {player}, {gamemode}.") String updatedOther,
    @Comment("Placeholders: {gamemode}.") String alreadyInMode,
    @Comment("Placeholders: {player}, {gamemode}.") String alreadyInModeOther,
    @Comment("Display name per gamemode.") Map<GameMode, String> names) {

  public static GamemodeConfig defaults() {
    return new GamemodeConfig(
        "<green>Your gamemode is now <gold>{gamemode}</gold>.",
        "<green><gold>{player}</gold>'s gamemode is now <gold>{gamemode}</gold>.",
        "<red>You are already in <gold>{gamemode}</gold> mode.",
        "<red><gold>{player}</gold> is already in <gold>{gamemode}</gold> mode.",
        Map.of(
            GameMode.SURVIVAL, "Survival",
            GameMode.CREATIVE, "Creative",
            GameMode.ADVENTURE, "Adventure",
            GameMode.SPECTATOR, "Spectator"));
  }

  public MessagePair whenUpdated(GameMode mode) {
    return new MessagePair(
        updated.replace("{gamemode}", nameOf(mode)),
        updatedOther.replace("{gamemode}", nameOf(mode)));
  }

  public MessagePair whenAlreadyInMode(GameMode mode) {
    return new MessagePair(
        alreadyInMode.replace("{gamemode}", nameOf(mode)),
        alreadyInModeOther.replace("{gamemode}", nameOf(mode)));
  }

  private static String capitalize(String raw) {
    return raw.charAt(0) + raw.substring(1).toLowerCase(Locale.ROOT);
  }

  private String nameOf(GameMode mode) {
    return names.getOrDefault(mode, capitalize(mode.name()));
  }
}
