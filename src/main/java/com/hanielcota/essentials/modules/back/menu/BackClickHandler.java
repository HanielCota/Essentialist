package com.hanielcota.essentials.modules.back.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.ItemClickHandler;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.HistoryEntry;
import com.hanielcota.essentials.modules.teleport.service.TeleportService;
import lombok.NonNull;
import org.bukkit.Bukkit;

public record BackClickHandler(
    ConfigHandle<BackConfig> config, TeleportHistory history, TeleportService teleport)
    implements ItemClickHandler<HistoryEntry> {

  public BackClickHandler(
      @NonNull ConfigHandle<BackConfig> config,
      @NonNull TeleportHistory history,
      @NonNull TeleportService teleport) {
    this.config = config;
    this.history = history;
    this.teleport = teleport;
  }

  @Override
  public void handle(@NonNull ClickContext click, @NonNull HistoryEntry entry) {
    var player = click.player();
    var playerId = player.getUniqueId();
    var entryId = entry.id();

    var snap = config.value();
    var target = entry.location();
    var world = target.getWorld();

    click.close();

    if (world == null) {
      history.remove(playerId, entryId);
      click.reply(snap.noBack());
      return;
    }

    var worldName = world.getName();
    if (Bukkit.getWorld(worldName) == null) {
      history.remove(playerId, entryId);
      click.reply(snap.noBack());
      return;
    }

    if (!teleport.teleportTo(player, target)) {
      click.reply(snap.noBack());
      return;
    }

    history.remove(playerId, entryId);

    var x = target.getX();
    var y = target.getY();
    var z = target.getZ();
    var successMessage = snap.formatBack(worldName, x, y, z);

    click.reply(successMessage);
  }
}
