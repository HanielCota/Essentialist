package com.hanielcota.essentials.modules.back.service;

import com.hanielcota.essentials.scheduler.Scheduler;
import com.hanielcota.essentials.scheduler.Task;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.bukkit.entity.Player;

public final class BackWarmupService {

  private final Map<UUID, Pending> pending = new ConcurrentHashMap<>();
  private final Scheduler scheduler;

  public BackWarmupService(@NonNull Scheduler scheduler) {
    this.scheduler = scheduler;
  }

  public void schedule(
      @NonNull Player player,
      @NonNull Duration delay,
      @NonNull Runnable onComplete,
      @NonNull Runnable onCancel) {
    var playerId = player.getUniqueId();
    cancel(playerId, true);

    var token = UUID.randomUUID();

    var task =
        this.scheduler.runOnEntityLater(player, () -> complete(playerId, token, onComplete), delay);

    var scheduled = new Pending(task, onCancel, token);
    this.pending.put(playerId, scheduled);
  }

  public void cancel(@NonNull UUID playerId, boolean notify) {
    var scheduled = this.pending.remove(playerId);
    if (scheduled == null) {
      return;
    }

    scheduled.task.cancel();

    if (notify) {
      scheduled.onCancel.run();
    }
  }

  public boolean isPending(@NonNull UUID playerId) {
    return this.pending.containsKey(playerId);
  }

  private void complete(@NonNull UUID playerId, @NonNull UUID token, @NonNull Runnable onComplete) {
    var scheduled = this.pending.get(playerId);
    if (scheduled == null || !scheduled.token.equals(token)) {
      return;
    }

    var removed = this.pending.remove(playerId, scheduled);
    if (!removed) {
      return;
    }

    onComplete.run();
  }

  private record Pending(Task task, Runnable onCancel, UUID token) {}
}
