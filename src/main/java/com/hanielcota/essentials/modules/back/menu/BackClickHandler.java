package com.hanielcota.essentials.modules.back.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.HistoryEntry;
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
      click.reply(snap.noBack());
      return;
    }

    player
        .teleportAsync(target)
        .thenAccept(
            success -> {
              if (!Boolean.TRUE.equals(success)) {
                click.reply(snap.noBack());
                return;
              }
              this.history.remove(playerId, entryId);
              var successMessage =
                  snap.formatBack(world.getName(), target.getX(), target.getY(), target.getZ());
              click.reply(successMessage);
            });
  }
}
