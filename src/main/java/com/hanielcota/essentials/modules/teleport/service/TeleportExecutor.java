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
    if (!player.teleport(destination)) {
      callback.onFailed();
      return;
    }
    callback.onSuccess();
  }
}
