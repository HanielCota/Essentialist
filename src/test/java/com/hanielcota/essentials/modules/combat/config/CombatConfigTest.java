package com.hanielcota.essentials.modules.combat.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class CombatConfigTest {

  private static CombatConfig config(
      CombatConfig.WorldMode mode, List<String> worlds, List<String> causes) {
    return new CombatConfig(true, mode, worlds, "", true, causes, false, false, false);
  }

  @Test
  void whitelistAppliesOnlyToListedWorlds() {
    var snap = config(CombatConfig.WorldMode.WHITELIST, List.of("arena"), List.of());

    assertTrue(snap.appliesTo("arena"));
    assertFalse(snap.appliesTo("world"));
  }

  @Test
  void blacklistAppliesEverywhereExceptListedWorlds() {
    var snap = config(CombatConfig.WorldMode.BLACKLIST, List.of("spawn"), List.of());

    assertFalse(snap.appliesTo("spawn"));
    assertTrue(snap.appliesTo("world"));
  }

  @Test
  void damageImmunityMatchesListedCauses() {
    var snap = config(CombatConfig.WorldMode.BLACKLIST, List.of(), List.of("FALL", "DROWNING"));

    assertTrue(snap.isDamageCauseImmune("FALL"));
    assertTrue(snap.isDamageCauseImmune("DROWNING"));
    assertFalse(snap.isDamageCauseImmune("FIRE"));
  }

  @Test
  void defaultsLeavePvpEnabledAndNoImmunities() {
    var snap = CombatConfig.defaults();

    assertTrue(snap.pvp());
    assertFalse(snap.isDamageCauseImmune("FALL"));
    assertFalse(snap.keepInventory());
  }
}
