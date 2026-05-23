package com.hanielcota.essentials.modules.homes.rename;

import com.hanielcota.essentials.scheduler.Task;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/**
 * Per-player rename sessions: which home is being renamed and the scheduled timeout that fires if
 * the player never sends a chat message.
 *
 * <p>Pure POJO state — start (replacing any prior), consume on completion, cancel on demand. Quit
 * cleanup lives in {@code HomeTeleportListener} per SRP.
 */
public final class HomeRenameSessions {

  private final ConcurrentHashMap<UUID, Pending> pending = new ConcurrentHashMap<>();

  public void start(@NonNull UUID player, @NonNull String homeName, @NonNull Task timeoutTask) {
    var prior = pending.put(player, new Pending(homeName, timeoutTask));

    if (prior != null) {
      prior.timeoutTask().cancel();
    }
  }

  public Pending consume(@NonNull UUID player) {
    return pending.remove(player);
  }

  public void cancel(@NonNull UUID player) {
    var prior = pending.remove(player);

    if (prior != null) {
      prior.timeoutTask().cancel();
    }
  }

  public record Pending(@NonNull String homeName, @NonNull Task timeoutTask) {}
}
