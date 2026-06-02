package com.hanielcota.essentials.modules.gamemode.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.GameMode;
import org.junit.jupiter.api.Test;

class GamemodeConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = GamemodeConfig.defaults();

    assertTrue(!snap.updated().isEmpty());
    assertTrue(!snap.updatedOther().isEmpty());
    assertTrue(!snap.alreadyInMode().isEmpty());
    assertTrue(!snap.alreadyInModeOther().isEmpty());
  }

  @Test
  void defaultsHaveNamesForAllGameModes() {
    var snap = GamemodeConfig.defaults();
    var names = snap.names();

    assertTrue(names.containsKey(GameMode.SURVIVAL));
    assertTrue(names.containsKey(GameMode.CREATIVE));
    assertTrue(names.containsKey(GameMode.ADVENTURE));
    assertTrue(names.containsKey(GameMode.SPECTATOR));
  }

  @Test
  void whenUpdatedReplacesGamemode() {
    var snap = GamemodeConfig.defaults();
    var pair = snap.whenUpdated(GameMode.CREATIVE);

    assertTrue(pair.forSender(true, "Player").contains("Creative"));
  }

  @Test
  void whenAlreadyInModeReplacesGamemode() {
    var snap = GamemodeConfig.defaults();
    var pair = snap.whenAlreadyInMode(GameMode.SURVIVAL);

    assertTrue(pair.forSender(true, "Player").contains("Survival"));
  }

  @Test
  void fallbackCapitalizesUnknownGamemode() {
    var snap = GamemodeConfig.defaults();
    var pair = snap.whenUpdated(GameMode.SURVIVAL);
    var msg = pair.forSender(true, "Player");

    assertTrue(msg.contains("Survival"));
  }
}
