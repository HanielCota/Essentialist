package com.hanielcota.essentials.modules.weather;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.weather.config.WeatherConfig;
import com.hanielcota.essentials.modules.weather.listener.ThunderChangeListener;
import com.hanielcota.essentials.modules.weather.listener.WeatherChangeListener;
import lombok.NonNull;
import org.bukkit.Bukkit;

public final class WeatherModule extends AbstractModule {

  public WeatherModule() {
    super("weather");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("weather", WeatherConfig.class, WeatherConfig::defaults);

    clearExistingWeather(config.value());

    registrar.listener(new WeatherChangeListener(config));
    registrar.listener(new ThunderChangeListener(config));
  }

  private void clearExistingWeather(@NonNull WeatherConfig snap) {
    for (var world : Bukkit.getWorlds()) {
      if (!snap.isDisabled(world.getName())) {
        continue;
      }

      world.setStorm(false);
      world.setThundering(false);
    }
  }
}
