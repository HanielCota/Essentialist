package com.hanielcota.essentials.modules.homes.listener;

import com.hanielcota.essentials.modules.homes.menu.HomesActionTarget;
import com.hanielcota.essentials.modules.homes.rename.HomeRenameSessions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Drops the player's per-session home state on quit so it does not leak across reconnects: their
 * {@link HomesActionTarget} entry and any in-flight {@link HomeRenameSessions} prompt.
 */
@RequiredArgsConstructor
public final class HomeTeleportListener implements Listener {

  private final HomesActionTarget actionTarget;
  private final HomeRenameSessions renameSessions;

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var uuid = event.getPlayer().getUniqueId();

    this.actionTarget.clear(uuid);
    this.renameSessions.cancel(uuid);
  }
}
