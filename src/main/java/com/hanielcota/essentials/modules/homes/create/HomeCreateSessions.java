package com.hanielcota.essentials.modules.homes.create;

import com.hanielcota.essentials.scheduler.Task;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.bukkit.Location;

/**
 * Per-player create-home sessions: the location captured the moment the player clicked "+ Nova
 * home" plus the scheduled timeout that fires if no chat message arrives.
 *
 * <p>Pure POJO state — start (replacing any prior), consume on completion, cancel on demand. Quit
 * cleanup lives in {@code HomesSessionCleanupListener} per SRP.
 */
public final class HomeCreateSessions {

  private final ConcurrentHashMap<UUID, Pending> pending = new ConcurrentHashMap<>();

  public void start(@NonNull UUID player, @NonNull Location location, @NonNull Task timeoutTask) {
    var prior = this.pending.put(player, new Pending(location, timeoutTask));

    if (prior != null) {
      prior.timeoutTask().cancel();
    }
  }

  public Pending consume(@NonNull UUID player) {
    return this.pending.remove(player);
  }

  public void cancel(@NonNull UUID player) {
    var prior = this.pending.remove(player);

    if (prior != null) {
      prior.timeoutTask().cancel();
    }
  }

  public record Pending(@NonNull Location location, @NonNull Task timeoutTask) {}
}
