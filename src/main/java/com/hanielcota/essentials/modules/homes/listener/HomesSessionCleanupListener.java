package com.hanielcota.essentials.modules.homes.listener;

import com.hanielcota.essentials.modules.homes.create.HomeCreateSessions;
import com.hanielcota.essentials.modules.homes.menu.HomesActionTarget;
import com.hanielcota.essentials.modules.homes.rename.HomeRenameSessions;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Drops the player's per-session home state on quit so it does not leak across reconnects: their
 * {@link HomesActionTarget} entry plus any in-flight rename or create chat prompt.
 */
@RequiredArgsConstructor
public final class HomesSessionCleanupListener implements Listener {

  private final HomesActionTarget actionTarget;
  private final HomeRenameSessions renameSessions;
  private final HomeCreateSessions createSessions;

  @EventHandler
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var uuid = event.getPlayer().getUniqueId();

    this.actionTarget.clear(uuid);
    this.renameSessions.cancel(uuid);
    this.createSessions.cancel(uuid);
  }
}
