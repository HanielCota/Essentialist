package com.hanielcota.essentials.modules.essentials.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.essentials.config.ModulesFilterConfig;
import com.hanielcota.essentials.modules.essentials.config.ModulesMenuConfig;
import org.junit.jupiter.api.Test;

class ModulesMenuLayoutTest {

  @Test
  void resolveWithDefaultsReservesControlSlotsOutOfContent() {
    var layout = ModulesMenuLayout.resolve(ModulesMenuConfig.defaults(), 6);

    assertEquals(4, layout.infoSlot());
    assertEquals(45, layout.filterSlot());

    var content = layout.contentSlots();
    assertTrue(content.contains(10));
    assertFalse(content.contains(layout.infoSlot()));
    assertFalse(content.contains(layout.filterSlot()));
    assertFalse(content.contains(48));
    assertFalse(content.contains(50));
  }

  @Test
  void resolveMovesFilterOffACollidingControlSlot() {
    var menu = menuWithFilterSlot(48); // default previous-page button slot

    var layout = ModulesMenuLayout.resolve(menu, 6);

    assertNotEquals(48, layout.filterSlot());
    assertNotEquals(50, layout.filterSlot());
    assertNotEquals(layout.infoSlot(), layout.filterSlot());
    assertFalse(layout.contentSlots().contains(layout.filterSlot()));
  }

  private static ModulesMenuConfig menuWithFilterSlot(int slot) {
    var base = ModulesMenuConfig.defaults();
    var f = base.filter();

    var collidingFilter =
        new ModulesFilterConfig(
            slot,
            f.material(),
            f.name(),
            f.lore(),
            f.activeMarker(),
            f.labelAll(),
            f.labelProtection(),
            f.labelTeleport(),
            f.labelChat(),
            f.labelItems(),
            f.labelPlayer(),
            f.labelAdmin(),
            f.labelOther());

    return new ModulesMenuConfig(
        base.title(),
        base.rows(),
        base.contentSlots(),
        base.enabledMaterial(),
        base.disabledMaterial(),
        base.enabledName(),
        base.disabledName(),
        base.enabledLore(),
        base.disabledLore(),
        base.pendingLine(),
        base.toggledOn(),
        base.toggledOff(),
        base.info(),
        collidingFilter,
        base.navigation());
  }
}
