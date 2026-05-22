package com.hanielcota.essentials.user;

import java.util.Objects;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class UserSessionListener implements Listener {

  private final UserService users;
  private final UserSessionService sessions;

  public UserSessionListener(UserService users, UserSessionService sessions) {
    this.users = Objects.requireNonNull(users, "users");
    this.sessions = Objects.requireNonNull(sessions, "sessions");
  }

  @EventHandler(priority = EventPriority.LOWEST)
  public void onJoin(PlayerJoinEvent event) {
    var player = event.getPlayer();
    var user = users.getOrCreate(player.getUniqueId(), player.getName());
    sessions.openSession(user, player.locale());
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onQuit(PlayerQuitEvent event) {
    sessions.closeSession(event.getPlayer().getUniqueId());
  }
}
