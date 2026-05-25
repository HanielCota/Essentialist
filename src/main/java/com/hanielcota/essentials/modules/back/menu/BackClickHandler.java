package com.hanielcota.essentials.modules.back.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.HistoryEntry;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.NonNull;

public record BackClickHandler(ConfigHandle<BackConfig> config, TeleportHistory history) {

  public void handle(@NonNull ClickContext click, @NonNull HistoryEntry entry) {
    var player = click.player();
    var playerId = player.getUniqueId();
    var entryId = entry.id();

    var snap = this.config.value();
    var target = entry.location();
    var world = target.getWorld();

    click.close();

    // SqliteTeleportHistory.readEntry filters rows whose world is no longer loaded, so
    // target.getWorld() is normally non-null. Keep one defensive check — if it ever is null the
    // entry is stale and should be evicted.
    if (world == null) {
      this.history.remove(playerId, entryId);

      var noBackMsg = snap.noBack();
      click.reply(noBackMsg);
      return;
    }

    var worldName = world.getName();
    var x = target.getX();
    var y = target.getY();
    var z = target.getZ();

    Consumer<Boolean> onResult =
        success -> onTeleportResult(success, click, snap, playerId, entryId, worldName, x, y, z);

    var teleportFuture = player.teleportAsync(target);
    teleportFuture.thenAccept(onResult);
  }

  private void onTeleportResult(
      Boolean success,
      @NonNull ClickContext click,
      @NonNull BackConfig snap,
      @NonNull UUID playerId,
      long entryId,
      @NonNull String worldName,
      double x,
      double y,
      double z) {
    if (!Boolean.TRUE.equals(success)) {
      var noBackMsg = snap.noBack();
      click.reply(noBackMsg);
      return;
    }

    this.history.remove(playerId, entryId);

    var successMessage = snap.formatBack(worldName, x, y, z);
    click.reply(successMessage);
  }
}
