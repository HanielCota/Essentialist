package com.hanielcota.essentials.modules.crops.listener;

import com.hanielcota.essentials.modules.crops.CropsNotifier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/** Evicts a player's notifier throttle timestamp on quit so the throttle map stays bounded. */
@RequiredArgsConstructor
public final class CropsNotifierCleanupListener implements Listener {

  private final CropsNotifier notifier;

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var player = event.getPlayer();
    var id = player.getUniqueId();

    this.notifier.forget(id);
  }
}
