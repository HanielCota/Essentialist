package com.hanielcota.essentials.paper;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * Best-effort player-name resolution shared by commands that need to show a name when only the UUID
 * is known. Resolution order: caller-supplied online hint → online lookup → offline lookup → UUID
 * string fallback.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PlayerNames {

  public static String nameOf(@NonNull PlayerProvider players, @NonNull UUID id) {
    return resolve(players, id, null);
  }

  public static String nameOf(
      @NonNull PlayerProvider players, @NonNull UUID id, Player onlineHint) {
    return resolve(players, id, onlineHint);
  }

  private static String resolve(
      @NonNull PlayerProvider players, @NonNull UUID id, Player onlineHint) {
    if (onlineHint != null) {
      return onlineHint.getName();
    }

    var online = players.online(id).orElse(null);
    if (online != null) {
      return online.getName();
    }

    var offline = players.offline(id);
    var stored = offline.getName();

    return stored != null ? stored : id.toString();
  }
}
