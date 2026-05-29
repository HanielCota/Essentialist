package com.hanielcota.essentials.modules.seen.config;

import com.hanielcota.essentials.modules.seen.domain.SeenLine;
import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record SeenConfig(
    @Comment("Shown for an online target. Placeholders: {player}, {duration}.") String online,
    @Comment("Shown for an offline target. Placeholders: {player}, {duration}.") String offline,
    @Comment("Shown when the target has never logged into the server. Placeholder: {player}.")
        String neverSeen) {

  public static SeenConfig defaults() {
    return new SeenConfig(
        "<gold>{player}</gold> <gray>is online now (joined <gold>{duration}</gold> ago).",
        "<gold>{player}</gold> <gray>was last seen <gold>{duration}</gold> ago.",
        "<red><gold>{player}</gold> has never joined the server.");
  }

  public String formatOnline(@NonNull String player, @NonNull String duration) {
    var withPlayer = online.replace("{player}", player);

    return withPlayer.replace("{duration}", duration);
  }

  public String formatOffline(@NonNull String player, @NonNull String duration) {
    var withPlayer = offline.replace("{player}", player);

    return withPlayer.replace("{duration}", duration);
  }

  public String formatNeverSeen(@NonNull String player) {
    return neverSeen.replace("{player}", player);
  }

  public String formatLine(@NonNull SeenLine line) {
    var name = line.displayName();
    var duration = line.duration();

    return switch (line.kind()) {
      case ONLINE -> formatOnline(name, duration);
      case OFFLINE -> formatOffline(name, duration);
    };
  }
}
