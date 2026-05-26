package com.hanielcota.essentials.modules.silencer.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.silencer.config.SilencerConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

@RequiredArgsConstructor
public final class AdvancementMessageListener implements Listener {

  private final ConfigHandle<SilencerConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onAdvancement(@NonNull PlayerAdvancementDoneEvent event) {
    var snap = this.config.value();
    if (!snap.suppressAdvancement()) {
      return;
    }

    // Already null for recipe unlocks / root advancements; setting null again is harmless.
    event.message(null);
  }
}
