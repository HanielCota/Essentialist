package com.hanielcota.essentials.modules.homes.rename;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.homes.config.messages.HomesMessages;
import com.hanielcota.essentials.modules.homes.service.HomeService.RenameResult;
import org.junit.jupiter.api.Test;

class HomeRenameMessagesTest {

  @Test
  void formatsPromptWithUnlimitedTimeoutLabel() {
    var line = HomeRenameMessages.prompt(HomesMessages.defaults(), "base", 0);

    assertTrue(line.contains("base"));
    assertTrue(line.contains("sem limite"));
  }

  @Test
  void formatsResultForEachRenameOutcome() {
    var messages = HomesMessages.defaults();

    assertEquals(
        "<green>Home <gold>old</gold> renomeada para <gold>new</gold>.",
        HomeRenameMessages.result(messages, "old", "new", RenameResult.RENAMED));
    assertEquals(
        "<red>A home <gold>old</gold> desapareceu antes da renomeação.",
        HomeRenameMessages.result(messages, "old", "new", RenameResult.NOT_FOUND));
    assertEquals(
        "<red>Você já tem uma home chamada <gold>new</gold>.",
        HomeRenameMessages.result(messages, "old", "new", RenameResult.NAME_TAKEN));
  }
}
