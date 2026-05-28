package com.hanielcota.essentials.modules.homes.config.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.homes.menu.presentation.HomeMenuPlaceholders;
import com.hanielcota.essentials.modules.homes.menu.presentation.HomesMainMenuSection;
import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialPickerSection;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class HomesMenuConfigTest {

  @Test
  void staticPickerTitleDoesNotRenderUnknownHomePlaceholderAsQuestionMark() {
    var config = HomesMenuConfig.defaults();

    assertEquals("<dark_gray>Pick an icon", MaterialPickerSection.staticTitle(config.picker()));
  }

  @Test
  void categoryBackButtonIsEnabledByDefault() {
    var config = HomesMenuConfig.defaults();

    assertTrue(config.category().backEnabled());
  }

  @Test
  void homeItemLoreRendersDirectionAndCreationTime() {
    var config = HomesMenuConfig.defaults();
    var mainMenu = config.main();
    var moment = LocalDateTime.of(2026, 5, 25, 15, 42);
    var millis = moment.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    var placeholders = HomeMenuPlaceholders.of("world", 10, 64, -20, 180, millis, 0L, 0L, mainMenu);

    var lore = HomesMainMenuSection.itemLore(mainMenu, placeholders);

    assertEquals("<gray>Direction: <white>Norte", lore[2]);
    assertEquals("<gray>Created: <white>25/05/2026 15:42", lore[3]);
  }
}
