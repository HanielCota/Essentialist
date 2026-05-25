package com.hanielcota.essentials.modules.homes.name;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import org.junit.jupiter.api.Test;

class HomeNameResolverTest {

  private static ConfigHandle<HomesConfig> config(HomesConfig value) {
    return new ConfigHandle<>() {
      @Override
      public String name() {
        return "homes";
      }

      @Override
      public HomesConfig value() {
        return value;
      }

      @Override
      public void reload() {}

      @Override
      public AutoCloseable onReload(java.util.function.Consumer<HomesConfig> listener) {
        return () -> {};
      }
    };
  }

  @Test
  void usesConfiguredDefaultWhenArgumentIsBlank() {
    var resolver = new HomeNameResolver(config(HomesConfig.defaults()), new HomeNameValidator());

    assertEquals("home", resolver.resolve(""));
  }

  @Test
  void rejectsInvalidNamesAfterDefaultResolution() {
    var resolver = new HomeNameResolver(config(HomesConfig.defaults()), new HomeNameValidator());

    assertNull(resolver.resolve("<red>base"));
  }
}
