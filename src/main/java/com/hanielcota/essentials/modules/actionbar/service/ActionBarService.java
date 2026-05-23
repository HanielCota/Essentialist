package com.hanielcota.essentials.modules.actionbar.service;

import com.hanielcota.essentials.util.ComponentUtils;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class ActionBarService {

  public void send(@NonNull Player player, @NonNull String message) {
    var bar = ComponentUtils.mini(message);
    player.sendActionBar(bar);
  }

  public int broadcast(@NonNull String message) {
    var bar = ComponentUtils.mini(message);
    var players = Bukkit.getOnlinePlayers();

    for (var player : players) {
      player.sendActionBar(bar);
    }
    return players.size();
  }
}
