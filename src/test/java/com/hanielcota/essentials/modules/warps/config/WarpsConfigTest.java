package com.hanielcota.essentials.modules.warps.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.hanielcota.essentials.modules.warps.domain.Warp;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class WarpsConfigTest {

  private static Warp warp(String name, Material storedIcon) {
    return new Warp(name, "world", 0, 64, 0, 0, 0, 0L, UUID.randomUUID(), storedIcon);
  }

  private static WarpsConfig config(Map<String, WarpMenuEntry> settings) {
    return new WarpsConfig(
        3,
        32,
        "[A-Za-z0-9_-]+",
        Material.ENDER_PEARL,
        10,
        WarpsMenuConfig.defaults(),
        settings,
        WarpsMessages.defaults());
  }

  private static WarpMenuEntry entry(Material icon, String name, List<String> lore, boolean pvp) {
    return new WarpMenuEntry(icon, name, lore, pvp);
  }

  @Test
  void configuredIconWinsAndIsCaseInsensitive() {
    var settings = Map.of("spawn", entry(Material.BEACON, null, List.of(), false));
    var config = config(settings);

    assertEquals(Material.BEACON, config.iconFor(warp("spawn", Material.STONE)));
    assertEquals(Material.BEACON, config.iconFor(warp("Spawn", Material.STONE)));
  }

  @Test
  void storedIconUsedWithoutEntryOrIcon() {
    assertEquals(Material.STONE, config(Map.of()).iconFor(warp("pvp", Material.STONE)));

    var noIconEntry = config(Map.of("a", entry(null, "X", List.of(), false)));
    assertEquals(Material.STONE, noIconEntry.iconFor(warp("a", Material.STONE)));
  }

  @Test
  void displayNameFallsBackToWarpName() {
    var config = config(Map.of("a", entry(null, "<yellow>Top", List.of(), false)));

    assertEquals("<yellow>Top", config.displayNameFor(warp("a", Material.STONE)));
    assertEquals("b", config.displayNameFor(warp("b", Material.STONE)));
  }

  @Test
  void loreComesFromEntryOrEmpty() {
    var config = config(Map.of("a", entry(null, null, List.of("l1", "l2"), false)));

    assertEquals(List.of("l1", "l2"), config.loreFor(warp("a", Material.STONE)));
    assertEquals(List.of(), config.loreFor(warp("b", Material.STONE)));
  }

  @Test
  void isPvpReadsTheEntryFlagCaseInsensitively() {
    var config = config(Map.of("arena", entry(null, null, List.of(), true)));

    assertTrue(config.isPvp("arena"));
    assertTrue(config.isPvp("Arena"));
    assertFalse(config.isPvp("spawn"));
  }

  @Test
  void defaultsAreSane() {
    var defaults = WarpsConfig.defaults();

    assertEquals(Material.ENDER_PEARL, defaults.defaultIcon());
    assertEquals(10, defaults.occupancyRadiusBlocks());
  }
}
