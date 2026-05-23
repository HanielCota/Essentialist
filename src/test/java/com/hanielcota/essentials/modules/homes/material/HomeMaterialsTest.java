package com.hanielcota.essentials.modules.homes.material;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class HomeMaterialsTest {

  @Test
  void parsesUnknownOrBlankIconAsDefault() {
    assertEquals(Material.RED_BED, HomeMaterials.parseIcon(""));
    assertEquals(Material.RED_BED, HomeMaterials.parseIcon("not_a_material"));
  }

  @Test
  void rejectsKnownNonRenderableMaterials() {
    assertFalse(HomeMaterials.isUsableIcon(Material.AIR));
    assertFalse(HomeMaterials.isUsableIcon(Material.WATER));
    assertTrue(HomeMaterials.isUsableIcon(Material.RED_BED));
  }

  @Test
  void sanitizesInvalidIconAsDefault() {
    assertEquals(Material.RED_BED, HomeMaterials.sanitizeIcon(Material.AIR));
    assertEquals(Material.RED_BED, HomeMaterials.sanitizeIcon(null));
  }
}
