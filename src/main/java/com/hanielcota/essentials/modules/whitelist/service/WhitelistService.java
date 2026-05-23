package com.hanielcota.essentials.modules.whitelist.service;

import java.util.Comparator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public final class WhitelistService {

  /** A player's name, falling back to the UUID string when the name is unknown. */
  public static String nameOf(OfflinePlayer player) {
    String name = player.getName();
    return name != null ? name : player.getUniqueId().toString();
  }

  /**
   * An online or cached player for {@code name}, or {@code null} if the server has never seen it.
   */
  private static OfflinePlayer resolveKnown(String name) {
    OfflinePlayer online = Bukkit.getPlayerExact(name);
    return online != null ? online : Bukkit.getOfflinePlayerIfCached(name);
  }

  /** A whitelisted player matching {@code name}, or {@code null} when none matches. */
  private static OfflinePlayer findWhitelisted(String name) {
    return Bukkit.getWhitelistedPlayers().stream()
        .filter(player -> name.equalsIgnoreCase(player.getName()))
        .findFirst()
        .orElse(null);
  }

  /** Whitelisted players, sorted by name (case-insensitive). */
  public List<OfflinePlayer> list() {
    return Bukkit.getWhitelistedPlayers().stream()
        .sorted(Comparator.comparing(WhitelistService::nameOf, String.CASE_INSENSITIVE_ORDER))
        .toList();
  }

  /**
   * Whitelists a player by name. Resolves only players the server already knows — online or in the
   * user cache; a name the server has never seen yields {@link AddResult#UNKNOWN_PLAYER}.
   */
  public AddResult add(String name) {

    OfflinePlayer player = resolveKnown(name);
    if (player == null) {
      return AddResult.UNKNOWN_PLAYER;
    }
    if (player.isWhitelisted()) {
      return AddResult.ALREADY_WHITELISTED;
    }
    player.setWhitelisted(true);
    return AddResult.ADDED;
  }

  /** Removes a player from the whitelist by name; returns {@code false} if not whitelisted. */
  public boolean remove(String name) {

    OfflinePlayer match = findWhitelisted(name);
    if (match == null) {
      return false;
    }
    match.setWhitelisted(false);
    return true;
  }

  /** Removes an already-resolved player from the whitelist. */
  public void remove(OfflinePlayer player) {
    player.setWhitelisted(false);
  }

  public enum AddResult {
    ADDED,
    ALREADY_WHITELISTED,
    UNKNOWN_PLAYER
  }
}
