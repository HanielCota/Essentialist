package com.hanielcota.essentials.modules.whitelist.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public final class WhitelistService {

  /** Whitelisted players, sorted by name (case-insensitive). */
  public List<OfflinePlayer> list() {
    return Bukkit.getWhitelistedPlayers().stream()
        .sorted(Comparator.comparing(WhitelistService::nameOf, String.CASE_INSENSITIVE_ORDER))
        .toList();
  }

  /** Whitelists {@code name}; returns {@code false} if it was already whitelisted. */
  @SuppressWarnings(
      "deprecation") // getOfflinePlayer(String): whitelisting is name-based by design.
  public boolean add(String name) {
    Objects.requireNonNull(name, "name");
    OfflinePlayer player = Bukkit.getOfflinePlayer(name);
    if (player.isWhitelisted()) {
      return false;
    }
    player.setWhitelisted(true);
    return true;
  }

  /** Removes {@code name} from the whitelist; returns {@code false} if it was not whitelisted. */
  @SuppressWarnings(
      "deprecation") // getOfflinePlayer(String): whitelisting is name-based by design.
  public boolean remove(String name) {
    Objects.requireNonNull(name, "name");
    OfflinePlayer player = Bukkit.getOfflinePlayer(name);
    if (!player.isWhitelisted()) {
      return false;
    }
    player.setWhitelisted(false);
    return true;
  }

  /** Removes an already-resolved player from the whitelist. */
  public void remove(OfflinePlayer player) {
    Objects.requireNonNull(player, "player");
    player.setWhitelisted(false);
  }

  /** A player's name, falling back to the UUID string when the name is unknown. */
  public static String nameOf(OfflinePlayer player) {
    Objects.requireNonNull(player, "player");
    String name = player.getName();
    return name != null ? name : player.getUniqueId().toString();
  }
}
