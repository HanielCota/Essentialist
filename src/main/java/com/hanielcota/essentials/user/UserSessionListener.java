package com.hanielcota.essentials.user;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class UserSessionListener implements Listener {

  private final UserSessionService sessions;

  @EventHandler(priority = EventPriority.LOWEST)
  public void onJoin(@NonNull PlayerJoinEvent event) {
    var player = event.getPlayer();
    var playerId = player.getUniqueId();

    this.sessions.openSession(playerId);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var player = event.getPlayer();
    var playerId = player.getUniqueId();

    this.sessions.closeSession(playerId);
  }
}
