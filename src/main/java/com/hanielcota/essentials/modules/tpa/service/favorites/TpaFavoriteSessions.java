package com.hanielcota.essentials.modules.tpa.service.favorites;

import com.hanielcota.essentials.scheduler.Task;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/**
 * Per-player favorite-add prompts: the scheduled timeout that fires if the player never sends a
 * chat message after clicking the add-favorite button.
 *
 * <p>Mirrors the shape of the homes rename session holder — start (replacing any prior), consume on
 * completion, cancel on demand. Quit cleanup lives in {@code TpaFavoriteSessionCleanupListener}.
 */
public final class TpaFavoriteSessions {

  private final ConcurrentHashMap<UUID, Pending> pending = new ConcurrentHashMap<>();

  public void start(@NonNull UUID player, @NonNull Task timeoutTask) {
    var prior = this.pending.put(player, new Pending(timeoutTask));

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

  public boolean isPending(@NonNull UUID player) {
    return this.pending.containsKey(player);
  }

  public record Pending(@NonNull Task timeoutTask) {}
}
