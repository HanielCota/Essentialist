package com.hanielcota.essentials.modules.back.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

@RequiredArgsConstructor
public final class PlayerTeleportListener implements Listener {

  private final TeleportHistory history;
  private final ConfigHandle<BackConfig> config;

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onTeleport(@NonNull PlayerTeleportEvent event) {
    var cause = event.getCause();
    var snap = this.config.value();

    var causeName = cause.name();
    if (snap.isCauseBlacklisted(causeName)) {
      return;
    }

    var player = event.getPlayer();
    var uuid = player.getUniqueId();
    var originLocation = event.getFrom();

    this.history.push(uuid, originLocation, TeleportHistory.Cause.TELEPORT);
  }
}
