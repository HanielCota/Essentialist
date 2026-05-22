package com.hanielcota.essentials.modules.back.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.github.hanielcota.menuframework.api.ItemClickHandler;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.HistoryEntry;
import com.hanielcota.essentials.modules.teleport.service.TeleportService;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.jspecify.annotations.NonNull;

public record BackClickHandler(
    ConfigHandle<BackConfig> config, TeleportHistory history, TeleportService teleport)
    implements ItemClickHandler<HistoryEntry> {

  public BackClickHandler {
    Objects.requireNonNull(config, "config");
    Objects.requireNonNull(history, "history");
    Objects.requireNonNull(teleport, "teleport");
  }

  @Override
  public void handle(@NonNull ClickContext click, @NonNull HistoryEntry entry) {
    var snap = config.value();
    var target = entry.location();
    var world = target.getWorld();
    var playerId = click.player().getUniqueId();

    click.close();

    if (world == null || Bukkit.getWorld(world.getName()) == null) {
      history.remove(playerId, entry.id());
      click.reply(snap.noBack());
      return;
    }

    if (!teleport.teleportTo(click.player(), target)) {
      click.reply(snap.noBack());
      return;
    }

    history.remove(playerId, entry.id());
    click.reply(snap.formatBack(world.getName(), target.getX(), target.getY(), target.getZ()));
  }
}
