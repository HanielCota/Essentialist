package com.hanielcota.essentials.modules.tpa.listener;

import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaNotifier;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Reacts to a player disconnecting: cancels every teleport request they take part in. The resulting
 * notifications are delegated to {@link TpaNotifier}.
 */
@RequiredArgsConstructor
public final class TpaQuitListener implements Listener {

  private final TeleportRequestService service;
  private final TpaNotifier notifier;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onQuit(PlayerQuitEvent event) {
    var quitter = event.getPlayer().getUniqueId();
    var quitterName = event.getPlayer().getName();
    for (var request : service.cancelAllOf(quitter)) {
      notifier.notifyPartnerLeft(request, quitter, quitterName);
    }
  }
}
