package com.hanielcota.essentials.modules.weather.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class WeatherConfigTest {

  @Test
  void defaultsHaveDisabledWorldsList() {
    var snap = WeatherConfig.defaults();

    assertFalse(snap.disabledWorlds().isEmpty());
  }

  @Test
  void isDisabledReturnsTrueForListedWorld() {
    var snap = WeatherConfig.defaults();

    assertTrue(snap.isDisabled("world"));
  }

  @Test
  void isDisabledReturnsFalseForUnlistedWorld() {
    var snap = WeatherConfig.defaults();

    assertFalse(snap.isDisabled("world_nether"));
  }

  @Test
  void customDisabledWorldsPreserved() {
    var snap = new WeatherConfig(java.util.List.of("world_nether"));

    assertTrue(snap.disabledWorlds().contains("world_nether"));
  }
}
