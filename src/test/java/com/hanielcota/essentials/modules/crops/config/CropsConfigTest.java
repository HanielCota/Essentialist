package com.hanielcota.essentials.modules.crops.config;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.crops.config.CropsConfig.WorldMode;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.junit.jupiter.api.Test;

class CropsConfigTest {

  private static CropsConfig config(
      WorldMode mode, List<String> worlds, List<String> crops, List<String> mobs, String bypass) {
    return new CropsConfig(
        true,
        mode,
        worlds,
        bypass,
        crops,
        mobs,
        true,
        true,
        true,
        true,
        true,
        false,
        true,
        true,
        false,
        CropsMessages.defaults());
  }

  @Test
  void whitelistAppliesOnlyToListedWorlds() {
    var snap = config(WorldMode.WHITELIST, List.of("world"), List.of(), List.of(), "");

    assertTrue(snap.appliesTo("world"));
    assertFalse(snap.appliesTo("world_nether"));
  }

  @Test
  void blacklistAppliesEverywhereExceptListedWorlds() {
    var snap = config(WorldMode.BLACKLIST, List.of("world"), List.of(), List.of(), "");

    assertFalse(snap.appliesTo("world"));
    assertTrue(snap.appliesTo("world_nether"));
  }

  @Test
  void emptyCropListManagesAllCrops() {
    var snap = config(WorldMode.WHITELIST, List.of(), List.of(), List.of(), "");

    assertTrue(snap.isCropAllowed(Material.WHEAT));
    assertTrue(snap.isCropAllowed(Material.NETHER_WART));
  }

  @Test
  void nonEmptyCropListManagesOnlyListed() {
    var snap = config(WorldMode.WHITELIST, List.of(), List.of("WHEAT"), List.of(), "");

    assertTrue(snap.isCropAllowed(Material.WHEAT));
    assertFalse(snap.isCropAllowed(Material.CARROTS));
  }

  @Test
  void emptyMobListBlocksAllMobs() {
    var snap = config(WorldMode.WHITELIST, List.of(), List.of(), List.of(), "");

    assertTrue(snap.isMobBlocked(EntityType.RABBIT));
    assertTrue(snap.isMobBlocked(EntityType.ZOMBIE));
  }

  @Test
  void nonEmptyMobListBlocksOnlyListed() {
    var snap = config(WorldMode.WHITELIST, List.of(), List.of(), List.of("RABBIT"), "");

    assertTrue(snap.isMobBlocked(EntityType.RABBIT));
    assertFalse(snap.isMobBlocked(EntityType.ZOMBIE));
  }

  @Test
  void bypassPermissionPresenceReflectsBlankness() {
    var without = config(WorldMode.WHITELIST, List.of(), List.of(), List.of(), "");
    var with =
        config(WorldMode.WHITELIST, List.of(), List.of(), List.of(), "essentials.crops.bypass");

    assertFalse(without.hasBypassPermission());
    assertTrue(with.hasBypassPermission());
  }
}
