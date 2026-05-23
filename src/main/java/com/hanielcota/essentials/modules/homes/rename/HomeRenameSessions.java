package com.hanielcota.essentials.modules.homes.rename;

import com.hanielcota.essentials.scheduler.Task;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Per-player rename sessions: which home is being renamed and the scheduled timeout that fires if
 * the player never sends a chat message.
 *
 * <p>Pure POJO state — start (replacing any prior), consume on completion, cancel on demand. Quit
 * cleanup lives in {@code HomeTeleportListener} per SRP.
 */
public final class HomeRenameSessions {

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

  public record Pending(String homeName, Task timeoutTask) {}
}
