package com.hanielcota.essentials.modules.homes.menu.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hanielcota.essentials.modules.homes.config.messages.HomesMessages;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class MaterialPickerPresentationTest {

  @Test
  void formatsAppliedAndMissingHomeReplies() {
    var presentation = new MaterialPickerPresentation();
    var messages = HomesMessages.defaults();

    assertEquals(
        "<green>Ícone de <gold>base</gold> alterado para <gold>red bed</gold>.",
        presentation.reply(messages, "base", Material.RED_BED, true));
    assertEquals(
        "<red>Você não tem nenhuma home chamada <gold>base</gold>.",
        presentation.reply(messages, "base", Material.RED_BED, false));
  }
}
