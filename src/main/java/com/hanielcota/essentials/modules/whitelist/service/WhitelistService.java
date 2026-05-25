package com.hanielcota.essentials.modules.whitelist.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public final class WhitelistService {

  // Player name; falls back to UUID when the server has never resolved the name.
  public static String nameOf(@NonNull OfflinePlayer player) {
    var name = player.getName();
    if (name != null) {
      return name;
    }

    return player.getUniqueId().toString();
  }

  // Online or cached player for `name`; null when the server has never seen it.
  private static OfflinePlayer resolveKnown(@NonNull String name) {
    var online = Bukkit.getPlayerExact(name);
    if (online != null) {
      return online;
    }

    return Bukkit.getOfflinePlayerIfCached(name);
  }

  private static OfflinePlayer findWhitelisted(@NonNull String name) {
    var whitelisted = Bukkit.getWhitelistedPlayers();

    for (var player : whitelisted) {
      var playerName = player.getName();
      if (name.equalsIgnoreCase(playerName)) {
        return player;
      }
    }

    return null;
  }

  // Whitelisted players, sorted by name (case-insensitive).
  public List<OfflinePlayer> list() {
    var whitelisted = Bukkit.getWhitelistedPlayers();
    var sorted = new ArrayList<OfflinePlayer>(whitelisted);
    var byName = Comparator.comparing(WhitelistService::nameOf, String.CASE_INSENSITIVE_ORDER);

    sorted.sort(byName);

    return sorted;
  }

  // Resolves only players the server already knows (online or in the user cache).
  public AddResult add(@NonNull String name) {
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

  public boolean remove(@NonNull String name) {
    var match = findWhitelisted(name);
    if (match == null) {
      return false;
    }

    match.setWhitelisted(false);
    return true;
  }

  public void remove(@NonNull OfflinePlayer player) {
    player.setWhitelisted(false);
  }

  public enum AddResult {
    ADDED,
    ALREADY_WHITELISTED,
    UNKNOWN_PLAYER
  }
}
