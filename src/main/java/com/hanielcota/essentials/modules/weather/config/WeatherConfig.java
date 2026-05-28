package com.hanielcota.essentials.modules.weather.config;

import java.util.List;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record WeatherConfig(
    @Comment(
            "Worlds where weather (rain and thunder) is permanently suppressed."
                + " Use the exact world folder name.")
        List<String> disabledWorlds) {

  public static WeatherConfig defaults() {
    return new WeatherConfig(List.of("world"));
  }

  public boolean isDisabled(String worldName) {
    return disabledWorlds.contains(worldName);
  }
}
