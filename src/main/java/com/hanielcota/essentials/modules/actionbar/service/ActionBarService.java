package com.hanielcota.essentials.modules.actionbar.service;

import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.shared.ComponentUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public final class ActionBarService {

  private final PlayerProvider players;

  public void send(@NonNull Player player, @NonNull String message) {
    var bar = ComponentUtils.mini(message);
    player.sendActionBar(bar);
  }

  public int broadcast(@NonNull String message) {
    var bar = ComponentUtils.mini(message);
    var roster = this.players.all();

    for (var player : roster) {
      player.sendActionBar(bar);
    }

    return roster.size();
  }
}
