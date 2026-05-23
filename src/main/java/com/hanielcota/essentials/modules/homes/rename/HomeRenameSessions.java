package com.hanielcota.essentials.modules.homes.rename;

import com.hanielcota.essentials.scheduler.Task;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Per-player rename sessions: which home is being renamed and the scheduled timeout that fires if
 * the player never sends a chat message. Sole responsibility: lifecycle of the {@link Pending}
 * entry — start (replacing any prior one), consume on completion, cancel on quit.
 */
public final class HomeRenameSessions implements Listener {

  private final ConcurrentHashMap<UUID, Pending> pending = new ConcurrentHashMap<>();

  public void start(UUID player, String homeName, Task timeoutTask) {
    var prior = pending.put(player, new Pending(homeName, timeoutTask));
    if (prior != null) prior.timeoutTask().cancel();
  }

  public Optional<Pending> consume(UUID player) {
    return Optional.ofNullable(pending.remove(player));
  }

  public void cancel(UUID player) {
    var prior = pending.remove(player);
    if (prior != null) prior.timeoutTask().cancel();
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    cancel(event.getPlayer().getUniqueId());
  }

  public record Pending(String homeName, Task timeoutTask) {}
}
