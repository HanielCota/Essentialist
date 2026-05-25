package com.hanielcota.essentials.modules.homes.menu.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hanielcota.essentials.modules.homes.config.menu.HomesMenuConfig;
import java.lang.reflect.Constructor;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import org.junit.jupiter.api.Test;

class HomeMenuPlaceholdersTest {

  @Test
  void formatsCreatedDateAndTimeFromMillis() {
    var settings = HomesMenuConfig.defaults();
    var moment = LocalDateTime.of(2026, 5, 25, 15, 42);
    var millis = moment.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

    var placeholders = HomeMenuPlaceholders.of("world", 10, 64, -20, 0, millis, settings);

    assertEquals("25/05/2026", placeholders.createdDate());
    assertEquals("15:42", placeholders.createdTime());
    assertEquals("25/05/2026 15:42", placeholders.createdAt());
  }

  @Test
  void convertsMinecraftYawToCardinalDirection() {
    var settings = HomesMenuConfig.defaults();
    var millis = System.currentTimeMillis();

    assertEquals("Sul", HomeMenuPlaceholders.of("world", 0, 0, 0, 0, millis, settings).direction());
    assertEquals(
        "Oeste", HomeMenuPlaceholders.of("world", 0, 0, 0, 90, millis, settings).direction());
    assertEquals(
        "Norte", HomeMenuPlaceholders.of("world", 0, 0, 0, 180, millis, settings).direction());
    assertEquals(
        "Leste", HomeMenuPlaceholders.of("world", 0, 0, 0, -90, millis, settings).direction());
    assertEquals(
        "Sudoeste", HomeMenuPlaceholders.of("world", 0, 0, 0, 45, millis, settings).direction());
  }

  @Test
  void rendersConfiguredWorldDisplayNameCaseInsensitively() throws Exception {
    var settings = settingsWithWorldNames(Map.of("world", "spawn"));
    var millis = System.currentTimeMillis();

    var placeholders = HomeMenuPlaceholders.of("World", 0, 0, 0, 0, millis, settings);

    assertEquals("spawn", placeholders.world());
  }

  @Test
  void fallsBackToStoredWorldNameWhenDisplayNameIsNotConfigured() {
    var settings = HomesMenuConfig.defaults();
    var millis = System.currentTimeMillis();

    var placeholders = HomeMenuPlaceholders.of("arena", 0, 0, 0, 0, millis, settings);

    assertEquals("arena", placeholders.world());
  }

  private static HomesMenuConfig settingsWithWorldNames(Map<String, String> worldNames)
      throws Exception {
    var original = HomesMenuConfig.defaults();
    var components = HomesMenuConfig.class.getRecordComponents();
    var values = new Object[components.length];
    var types = new Class<?>[components.length];

    for (var i = 0; i < components.length; i++) {
      var component = components[i];
      types[i] = component.getType();
      values[i] = component.getAccessor().invoke(original);

      if ("worldNames".equals(component.getName())) {
        values[i] = worldNames;
      }
    }

    Constructor<HomesMenuConfig> constructor = HomesMenuConfig.class.getDeclaredConstructor(types);
    return constructor.newInstance(values);
  }
}
