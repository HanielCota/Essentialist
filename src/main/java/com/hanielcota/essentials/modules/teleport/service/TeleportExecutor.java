package com.hanielcota.essentials.modules.teleport.service;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Player;

final class TeleportExecutor {

  void teleport(
      @NonNull Player player,
      @NonNull Location destination,
      @NonNull DelayedTeleport.Callback callback) {
    if (!player.isOnline()) {
      callback.onCancelled();
      return;
    }
    // teleportAsync keeps chunk-load latency off the calling thread and completes the future on
    // the entity's owning thread, so the callback fires on the same thread the sync teleport would
    // have used.
    player
        .teleportAsync(destination)
        .thenAccept(
            success -> {
              if (Boolean.TRUE.equals(success)) {
                callback.onSuccess();
              } else {
                callback.onFailed();
              }
            });
  }
}
