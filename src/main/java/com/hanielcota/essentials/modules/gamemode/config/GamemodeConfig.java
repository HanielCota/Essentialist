package com.hanielcota.essentials.modules.gamemode.config;

import com.hanielcota.essentials.config.MessagePair;
import java.util.Locale;
import java.util.Map;
import lombok.NonNull;
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

  private static String capitalize(@NonNull String raw) {
    return raw.charAt(0) + raw.substring(1).toLowerCase(Locale.ROOT);
  }

  public MessagePair whenUpdated(@NonNull GameMode mode) {
    var displayName = nameOf(mode);
    var selfText = updated.replace("{gamemode}", displayName);
    var otherText = updatedOther.replace("{gamemode}", displayName);
    return new MessagePair(selfText, otherText);
  }

  public MessagePair whenAlreadyInMode(@NonNull GameMode mode) {
    var displayName = nameOf(mode);
    var selfText = alreadyInMode.replace("{gamemode}", displayName);
    var otherText = alreadyInModeOther.replace("{gamemode}", displayName);
    return new MessagePair(selfText, otherText);
  }

  private String nameOf(@NonNull GameMode mode) {
    var fallback = capitalize(mode.name());
    return names.getOrDefault(mode, fallback);
  }
}
