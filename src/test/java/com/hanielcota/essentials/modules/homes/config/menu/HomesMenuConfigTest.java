package com.hanielcota.essentials.modules.homes.config.menu;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HomesMenuConfigTest {

  @Test
  void staticPickerTitleDoesNotRenderUnknownHomePlaceholderAsQuestionMark() {
    var config = HomesMenuConfig.defaults();

    assertEquals("<dark_gray>Pick an icon", MaterialPickerSection.staticTitle(config));
  }
}
