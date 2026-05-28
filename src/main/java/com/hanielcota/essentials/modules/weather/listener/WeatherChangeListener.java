package com.hanielcota.essentials.modules.weather.listener;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.weather.config.WeatherConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

@RequiredArgsConstructor
public final class WeatherChangeListener implements Listener {

  private final ConfigHandle<WeatherConfig> config;

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onWeatherChange(@NonNull WeatherChangeEvent event) {
    if (!event.toWeatherState()) {
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
