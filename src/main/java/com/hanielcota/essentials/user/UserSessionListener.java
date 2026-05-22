package com.hanielcota.essentials.user;

import java.util.Objects;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class UserSessionListener implements Listener {

  private final UserSessionService sessions;

  public UserSessionListener(UserSessionService sessions) {
    this.sessions = Objects.requireNonNull(sessions, "sessions");
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onJoin(PlayerJoinEvent event) {
    sessions.openSession(event.getPlayer().getUniqueId());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onQuit(PlayerQuitEvent event) {
    sessions.closeSession(event.getPlayer().getUniqueId());
  }
}
