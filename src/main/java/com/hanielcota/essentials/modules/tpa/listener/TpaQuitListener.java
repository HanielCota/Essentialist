package com.hanielcota.essentials.modules.tpa.listener;

import com.hanielcota.essentials.modules.tpa.command.TpaNotifier;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaPendingSelections;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Reacts to a player disconnecting: cancels every teleport request they take part in and clears
 * their pending-action selection so the map doesn't keep stale request references. The resulting
 * notifications are delegated to {@link TpaNotifier}.
 */
@RequiredArgsConstructor
public final class TpaQuitListener implements Listener {

  private final TeleportRequestService service;
  private final TpaNotifier notifier;
  private final TpaPendingSelections pendingSelections;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var player = event.getPlayer();
    var quitterId = player.getUniqueId();
    var quitterName = player.getName();

    this.pendingSelections.clear(quitterId);

    var affected = this.service.cancelAllOf(quitterId);
    for (TeleportRequest request : affected) {
      this.notifier.notifyPartnerLeft(request, quitterId, quitterName);
    }
  }
}
