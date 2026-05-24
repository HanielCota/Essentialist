package com.hanielcota.essentials.modules.homes.menu.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.menu.MaterialNamesConfig;
import com.hanielcota.essentials.modules.homes.config.messages.HomesMessages;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class MaterialPickerPresentationTest {

  private static ConfigHandle<MaterialNamesConfig> materialNames(MaterialNamesConfig value) {
    return new ConfigHandle<>() {
      @Override
      public String name() {
        return "homes/material-names";
      }

      @Override
      public MaterialNamesConfig value() {
        return value;
      }

      @Override
      public void reload() {}
    };
  }

  @Test
  void formatsAppliedAndMissingHomeReplies() {
    var presentation =
        new MaterialPickerPresentation(materialNames(MaterialNamesConfig.defaults()));
    var messages = HomesMessages.defaults();

    assertEquals(
        "<green>Icon of <gold>base</gold> changed to <gold>Red Bed</gold>.",
        presentation.reply(messages, "base", Material.RED_BED, true));
    assertEquals(
        "<red>You don't have any home named <gold>base</gold>.",
        presentation.reply(messages, "base", Material.RED_BED, false));
  }
}
