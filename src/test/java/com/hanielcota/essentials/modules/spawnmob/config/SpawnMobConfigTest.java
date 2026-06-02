package com.hanielcota.essentials.modules.spawnmob.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class SpawnMobConfigTest {

  @Test
  void defaultsHavePositiveMaxPerCommand() {
    var snap = SpawnMobConfig.defaults();

    assertTrue(snap.maxPerCommand() > 0);
  }

  @Test
  void defaultsHaveAllMessages() {
    var snap = SpawnMobConfig.defaults();

    assertTrue(!snap.spawned().isEmpty());
    assertTrue(!snap.invalidMob().isEmpty());
  }

  @Test
  void formatSpawnedReplacesPlaceholders() {
    var snap = SpawnMobConfig.defaults();
    var msg = snap.formatSpawned(5, "zombie");

    assertTrue(msg.contains("5"));
    assertTrue(msg.contains("zombie"));
  }

  @Test
  void formatInvalidMobReplacesMob() {
    var snap = SpawnMobConfig.defaults();
    var msg = snap.formatInvalidMob("dragon");

    assertTrue(msg.contains("dragon"));
  }
}
