package com.hanielcota.essentials.modules.afk.listener;

import com.hanielcota.essentials.modules.afk.service.AfkService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Stamps the join time as the joiner's first activity so the auto checker doesn't treat brand-new
 * sessions as already idle.
 */
@RequiredArgsConstructor
public final class AfkJoinListener implements Listener {

  private final AfkService service;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onJoin(@NonNull PlayerJoinEvent event) {
    var id = event.getPlayer().getUniqueId();
    var now = System.currentTimeMillis();

    this.service.recordActivity(id, now);
  }
}
