package com.hanielcota.essentials.modules.back.listener;

import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import java.util.Objects;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class PlayerDeathListener implements Listener {

  private final TeleportHistory history;

  public PlayerDeathListener(TeleportHistory history) {
    this.history = Objects.requireNonNull(history, "history");
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onDeath(PlayerDeathEvent event) {
    var player = event.getEntity();
    var loc = player.getLocation();
    history.push(player.getUniqueId(), loc);
  }
}
