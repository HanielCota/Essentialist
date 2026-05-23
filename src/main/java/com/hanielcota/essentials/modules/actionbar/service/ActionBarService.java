package com.hanielcota.essentials.modules.actionbar.service;

import com.hanielcota.essentials.util.ComponentUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ActionBarService {

  public void send(Player player, String message) {
    player.sendActionBar(ComponentUtils.mini(message));
  }

  public int broadcast(String message) {
    var bar = ComponentUtils.mini(message);
    var players = Bukkit.getOnlinePlayers();

    for (var player : players) {
      player.sendActionBar(bar);
    }
    return players.size();
  }
}
