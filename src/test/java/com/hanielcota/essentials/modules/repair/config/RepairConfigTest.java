package com.hanielcota.essentials.modules.repair.config;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class RepairConfigTest {

  @Test
  void defaultsHaveAllMessages() {
    var snap = RepairConfig.defaults();

    assertTrue(!snap.repairedHand().isEmpty());
    assertTrue(!snap.repairedAll().isEmpty());
    assertTrue(!snap.nothingHand().isEmpty());
    assertTrue(!snap.nothingAll().isEmpty());
    assertTrue(!snap.emptyHand().isEmpty());
  }

  @Test
  void defaultsHaveSensibleRepairAllLimit() {
    var snap = RepairConfig.defaults();

    assertTrue(snap.repairAllLimit() > 0);
  }

  @Test
  void defaultsBlacklistIsEmpty() {
    var snap = RepairConfig.defaults();

    assertTrue(snap.blacklist().isEmpty());
  }

  @Test
  void whenHandRepairedReturnsPair() {
    var snap = RepairConfig.defaults();
    var pair = snap.whenHandRepaired();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
  }

  @Test
  void whenAllRepairedReturnsPair() {
    var snap = RepairConfig.defaults();
    var pair = snap.whenAllRepaired();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
  }

  @Test
  void whenNothingHandReturnsPair() {
    var snap = RepairConfig.defaults();
    var pair = snap.whenNothingHand();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
  }

  @Test
  void whenNothingAllReturnsPair() {
    var snap = RepairConfig.defaults();
    var pair = snap.whenNothingAll();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
  }

  @Test
  void whenEmptyHandReturnsPair() {
    var snap = RepairConfig.defaults();
    var pair = snap.whenEmptyHand();

    assertTrue(!pair.forSender(true, "Player").isEmpty());
  }

  @Test
  void blacklistedMaterialIsInList() {
    var snap =
        new RepairConfig(
            "rh",
            "rho",
            "ra",
            "rao",
            "nh",
            "nho",
            "na",
            "nao",
            "eh",
            "eho",
            List.of(Material.ELYTRA),
            41);

    assertTrue(snap.blacklist().contains(Material.ELYTRA));
  }
}
