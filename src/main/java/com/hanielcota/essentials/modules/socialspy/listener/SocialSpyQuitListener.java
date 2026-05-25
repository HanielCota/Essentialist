package com.hanielcota.essentials.modules.socialspy.listener;

import com.hanielcota.essentials.modules.socialspy.service.SocialSpyService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/** Drops the quitting player's spy entry so state doesn't outlive the session. */
@RequiredArgsConstructor
public final class SocialSpyQuitListener implements Listener {

  private final SocialSpyService service;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onQuit(@NonNull PlayerQuitEvent event) {
    var player = event.getPlayer();
    var id = player.getUniqueId();

    this.service.exit(id);
  }
}
