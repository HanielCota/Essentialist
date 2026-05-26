package com.hanielcota.essentials.modules.silencer.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.silencer.config.SilencerConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

@RequiredArgsConstructor
public final class DeathMessageListener implements Listener {

  private final ConfigHandle<SilencerConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onDeath(@NonNull PlayerDeathEvent event) {
    var snap = this.config.value();
    if (!snap.suppressDeath()) {
      return;
    }

    event.deathMessage(null);
  }
}
