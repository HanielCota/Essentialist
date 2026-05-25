package com.hanielcota.essentials.modules.homes.config.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.homes.menu.presentation.HomeMenuPlaceholders;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.junit.jupiter.api.Test;

class HomesMenuConfigTest {

  @Test
  void staticPickerTitleDoesNotRenderUnknownHomePlaceholderAsQuestionMark() {
    var config = HomesMenuConfig.defaults();

    assertEquals("<dark_gray>Pick an icon", MaterialPickerSection.staticTitle(config));
  }

  @Test
  void categoryBackButtonIsEnabledByDefault() {
    var config = HomesMenuConfig.defaults();

    assertTrue(config.categoryBackEnabled());
  }

  @Test
  void homeItemLoreRendersDirectionAndCreationTime() {
    var config = HomesMenuConfig.defaults();
    var moment = LocalDateTime.of(2026, 5, 25, 15, 42);
    var millis = moment.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    var placeholders = HomeMenuPlaceholders.of("world", 10, 64, -20, 180, millis, config);

    var lore = HomesMainMenuSection.itemLore(config, placeholders);

    assertEquals("<gray>Direction: <white>Norte", lore[2]);
    assertEquals("<gray>Created: <white>25/05/2026 15:42", lore[3]);
  }
}
