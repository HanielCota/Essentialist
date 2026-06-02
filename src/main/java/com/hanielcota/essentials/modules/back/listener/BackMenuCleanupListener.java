package com.hanielcota.essentials.modules.back.listener;

import com.hanielcota.essentials.modules.back.service.BackFilterState;
import com.hanielcota.essentials.modules.back.service.BackPrefetch;
import com.hanielcota.essentials.modules.back.service.BackStaffViewState;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class BackMenuCleanupListener implements Listener {

  private final BackPrefetch prefetch;
  private final BackFilterState filterState;
  private final BackStaffViewState staffViewState;

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var player = event.getPlayer();
    var playerId = player.getUniqueId();

    this.prefetch.clear(playerId);
    this.filterState.clear(playerId);
    this.staffViewState.endView(playerId);
  }
}
