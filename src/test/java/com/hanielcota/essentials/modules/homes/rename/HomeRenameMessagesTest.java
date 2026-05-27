package com.hanielcota.essentials.modules.homes.rename;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.homes.config.HomesMessages;
import com.hanielcota.essentials.modules.homes.service.HomeService.RenameResult;
import org.junit.jupiter.api.Test;

class HomeRenameMessagesTest {

  @Test
  void formatsPromptWithUnlimitedTimeoutLabel() {
    var line = HomeRenameMessages.prompt(HomesMessages.defaults(), "base", 0);

    assertTrue(line.contains("base"));
    assertTrue(line.contains("no limit"));
  }

  @Test
  void formatsResultForEachRenameOutcome() {
    var messages = HomesMessages.defaults();

    assertEquals(
        "<green>Home <gold>old</gold> renamed to <gold>new</gold>.",
        HomeRenameMessages.result(messages, "old", "new", RenameResult.RENAMED));
    assertEquals(
        "<red>Home <gold>old</gold> disappeared before the rename completed.",
        HomeRenameMessages.result(messages, "old", "new", RenameResult.NOT_FOUND));
    assertEquals(
        "<red>You already have a home named <gold>new</gold>.",
        HomeRenameMessages.result(messages, "old", "new", RenameResult.NAME_TAKEN));
  }
}
