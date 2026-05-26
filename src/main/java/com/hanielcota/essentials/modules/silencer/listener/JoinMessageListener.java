package com.hanielcota.essentials.modules.silencer.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.silencer.config.SilencerConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@RequiredArgsConstructor
public final class JoinMessageListener implements Listener {

  private final ConfigHandle<SilencerConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onJoin(@NonNull PlayerJoinEvent event) {
    var snap = this.config.value();
    if (!snap.suppressJoin()) {
      return;
    }

    event.joinMessage(null);
  }
}
