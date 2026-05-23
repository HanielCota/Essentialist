package com.hanielcota.essentials.modules.fly.listener;

import com.hanielcota.essentials.modules.fly.service.FlyService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class FlyQuitListener implements Listener {

  private final FlyService service;

  @EventHandler(priority = EventPriority.MONITOR)
  public void onQuit(@NonNull PlayerQuitEvent event) {
    this.service.forget(event.getPlayer().getUniqueId());
  }
}
