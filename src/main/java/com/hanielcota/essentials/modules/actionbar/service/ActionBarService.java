package com.hanielcota.essentials.modules.actionbar.service;

import com.hanielcota.essentials.util.ComponentUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ActionBarService {

  /** Sends the action bar message to a single player. */
  public void send(Player player, String message) {

    player.sendActionBar(ComponentUtils.mini(message));
  }

  /** Sends the action bar to every online player; returns how many received it. */
  public int broadcast(String message) {

    Component bar = ComponentUtils.mini(message);
    var players = Bukkit.getOnlinePlayers();
    for (Player player : players) {
      player.sendActionBar(bar);
    }
    return players.size();
  }
}
