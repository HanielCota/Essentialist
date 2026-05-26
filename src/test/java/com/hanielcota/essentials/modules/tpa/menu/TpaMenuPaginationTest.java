package com.hanielcota.essentials.modules.tpa.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hanielcota.essentials.modules.tpa.config.TpaBlockedMenuConfig;
import com.hanielcota.essentials.modules.tpa.config.TpaHelpMenuConfig;
import com.hanielcota.essentials.modules.tpa.config.TpaPendingMenuConfig;
import com.hanielcota.essentials.modules.tpa.config.TpaSettingsMenuConfig;
import java.util.List;
import org.junit.jupiter.api.Test;

class TpaMenuPaginationTest {

  @Test
  void helpMenuContentSlotsCoverEveryDynamicHubItem() {
    var settings = TpaHelpMenuConfig.defaults();
    var slots = TpaHelpMenu.contentSlots(settings, settings.rows());

    assertEquals(
        List.of(
            settings.profileSlot(),
            settings.tpaSlot(),
            settings.pendingSlot(),
            settings.historySlot(),
            settings.settingsSlot(),
            settings.favoritesSlot(),
            settings.outgoingSlot()),
        slots);
  }

  @Test
  void pendingMenuContentSlotsUseConfiguredRequestSlots() {
    var settings = TpaPendingMenuConfig.defaults();
    var slots = TpaPendingMenu.contentSlots(settings, settings.rows());

    assertEquals(settings.contentSlots(), slots);
  }

  @Test
  void pendingMenuConfigExposesBulkAndBackSlots() {
    var settings = TpaPendingMenuConfig.defaults();

    assertEquals(47, settings.acceptAllSlot());
    assertEquals(51, settings.denyAllSlot());
    assertEquals(49, settings.backSlot());
  }

  @Test
  void settingsMenuContentSlotsCoverEveryDynamicToggleItem() {
    var settings = TpaSettingsMenuConfig.defaults();
    var slots = TpaSettingsMenu.contentSlots(settings, settings.rows());

    assertEquals(
        List.of(
            settings.privacySlot(),
            settings.notificationsSlot(),
            settings.behaviorSlot(),
            settings.cooldownSlot(),
            settings.backSlot()),
        slots);
  }

  @Test
  void blockedMenuContentSlotsUseConfiguredBlockedSlots() {
    var settings = TpaBlockedMenuConfig.defaults();
    var slots = TpaBlockedMenu.contentSlots(settings, settings.rows());

    assertEquals(settings.contentSlots(), slots);
  }
}
