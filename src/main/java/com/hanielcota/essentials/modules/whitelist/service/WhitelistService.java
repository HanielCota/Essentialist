package com.hanielcota.essentials.modules.whitelist.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public final class WhitelistService {

  // Player name; falls back to UUID when the server has never resolved the name.
  public static String nameOf(OfflinePlayer player) {
    var name = player.getName();
    return name != null ? name : player.getUniqueId().toString();
  }

  // Online or cached player for `name`; null when the server has never seen it.
  private static OfflinePlayer resolveKnown(String name) {
    var online = Bukkit.getPlayerExact(name);
    return online != null ? online : Bukkit.getOfflinePlayerIfCached(name);
  }

  private static OfflinePlayer findWhitelisted(String name) {
    for (var player : Bukkit.getWhitelistedPlayers()) {
      if (name.equalsIgnoreCase(player.getName())) return player;
    }
    return null;
  }

  // Whitelisted players, sorted by name (case-insensitive).
  public List<OfflinePlayer> list() {
    var whitelisted = Bukkit.getWhitelistedPlayers();
    var sorted = new ArrayList<OfflinePlayer>(whitelisted);
    sorted.sort(Comparator.comparing(WhitelistService::nameOf, String.CASE_INSENSITIVE_ORDER));
    return sorted;
  }

  // Resolves only players the server already knows (online or in the user cache).
  public AddResult add(String name) {
    var player = resolveKnown(name);
    if (player == null) {
      return AddResult.UNKNOWN_PLAYER;
    }
    if (player.isWhitelisted()) {
      return AddResult.ALREADY_WHITELISTED;
    }

    player.setWhitelisted(true);
    return AddResult.ADDED;
  }

  public boolean remove(String name) {
    var match = findWhitelisted(name);
    if (match == null) {
      return false;
    }

    match.setWhitelisted(false);
    return true;
  }

  public void remove(OfflinePlayer player) {
    player.setWhitelisted(false);
  }

  public enum AddResult {
    ADDED,
    ALREADY_WHITELISTED,
    UNKNOWN_PLAYER
  }
}
