package com.hanielcota.essentials.modules.teleport.service;

import com.hanielcota.essentials.scheduler.Task;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

final class PendingTeleports {

  private final Map<UUID, PendingTeleport> pending = new ConcurrentHashMap<>();

  void put(@NonNull UUID player, @NonNull Task task, @NonNull DelayedTeleport.Callback callback) {
    this.pending.put(player, new PendingTeleport(task, callback));
  }

  @Nullable PendingTeleport remove(@NonNull UUID player) {
    return this.pending.remove(player);
  }

  void cancelSilently(@NonNull UUID player) {
    var pendingTeleport = remove(player);
    if (pendingTeleport != null) {
      pendingTeleport.cancelTask();
    }
  }
}
