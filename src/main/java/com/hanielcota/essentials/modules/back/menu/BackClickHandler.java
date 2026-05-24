package com.hanielcota.essentials.modules.back.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.HistoryEntry;
import lombok.NonNull;
import org.bukkit.Bukkit;

public record BackClickHandler(ConfigHandle<BackConfig> config, TeleportHistory history) {

  public void handle(@NonNull ClickContext click, @NonNull HistoryEntry entry) {
    var player = click.player();
    var playerId = player.getUniqueId();
    var entryId = entry.id();

    var snap = this.config.value();
    var target = entry.location();
    var world = target.getWorld();

    click.close();

    if (world == null) {
      this.history.remove(playerId, entryId);
      click.reply(snap.noBack());
      return;
    }

    var worldName = world.getName();
    if (Bukkit.getWorld(worldName) == null) {
      this.history.remove(playerId, entryId);
      click.reply(snap.noBack());
      return;
    }

    if (!player.teleport(target)) {
      click.reply(snap.noBack());
      return;
    }

    this.history.remove(playerId, entryId);

    var x = target.getX();
    var y = target.getY();
    var z = target.getZ();
    var successMessage = snap.formatBack(worldName, x, y, z);

    click.reply(successMessage);
  }
}
