package com.hanielcota.essentials.modules.homes.menu;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Tracks which home a player is currently acting on across the sub-flows opened from the /homes
 * menu (delete confirmation, material picker, chat-driven rename). The home name is captured when
 * the dispatch click fires, so the sub-flow does not need to look up which home was clicked.
 */
public final class HomesActionTarget implements Listener {

  private final ConcurrentHashMap<UUID, String> targets = new ConcurrentHashMap<>();

  public void set(UUID player, String homeName) {
    targets.put(player, homeName);
  }

  public Optional<String> peek(UUID player) {
    return Optional.ofNullable(targets.get(player));
  }

  public Optional<String> consume(UUID player) {
    return Optional.ofNullable(targets.remove(player));
  }

  public void clear(UUID player) {
    targets.remove(player);
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    targets.remove(event.getPlayer().getUniqueId());
  }
}
