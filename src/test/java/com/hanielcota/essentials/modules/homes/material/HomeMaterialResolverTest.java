package com.hanielcota.essentials.modules.homes.material;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class HomeMaterialResolverTest {

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
    };
  }

  @Test
  void usesConfiguredDefaultWhenArgumentIsBlank() {
    var resolver = new HomeMaterialResolver(config(HomesConfig.defaults()));

    assertEquals(Material.RED_BED, resolver.resolve(""));
  }

  @Test
  void rejectsUnknownOrNonItemMaterials() {
    var resolver =
        new HomeMaterialResolver(
            config(HomesConfig.defaults()), material -> material != Material.AIR);

    assertNull(resolver.resolve("not_a_material"));
    assertNull(resolver.resolve("air"));
  }
}
