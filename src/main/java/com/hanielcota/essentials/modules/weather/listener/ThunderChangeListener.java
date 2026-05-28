package com.hanielcota.essentials.modules.weather.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.weather.config.WeatherConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;

@RequiredArgsConstructor
public final class ThunderChangeListener implements Listener {

  private final ConfigHandle<WeatherConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onThunderChange(@NonNull ThunderChangeEvent event) {
    if (!event.toThunderState()) {
      return;
    }

    var snap = this.config.value();
    var worldName = event.getWorld().getName();

    if (!snap.isDisabled(worldName)) {
      return;
    }

    event.setCancelled(true);
  }
}
