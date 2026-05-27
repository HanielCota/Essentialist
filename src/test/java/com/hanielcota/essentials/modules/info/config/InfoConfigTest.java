package com.hanielcota.essentials.modules.info.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.bukkit.GameMode;
import org.junit.jupiter.api.Test;

class InfoConfigTest {

  private final InfoConfig defaults = InfoConfig.defaults();

  @Test
  void gameModeLabelExhaustivelyMapsEveryMode() {
    assertEquals("Sobrevivência", defaults.gameModeLabel(GameMode.SURVIVAL));
    assertEquals("Criativo", defaults.gameModeLabel(GameMode.CREATIVE));
    assertEquals("Aventura", defaults.gameModeLabel(GameMode.ADVENTURE));
    assertEquals("Espectador", defaults.gameModeLabel(GameMode.SPECTATOR));
  }

  @Test
  void defaultsReturnInRangeEffectiveSlots() {
    var rows = defaults.effectiveRows();
    var max = rows * 9;

    assertTrue(rows >= 1 && rows <= 6, "rows " + rows);
    assertTrue(within(defaults.effectiveBackSlot(), max), "back");
    assertTrue(within(defaults.effectiveServerSlot(), max), "server");
    assertTrue(within(defaults.effectivePlayerSlot(), max), "player");
    assertTrue(within(defaults.effectiveAboutSlot(), max), "about");
  }

  @Test
  void defaultsExposeNonEmptyEntrySections() {
    var server = defaults.server();
    var player = defaults.player();
    var plugin = defaults.plugin();

    assertEquals("agora mesmo", player.noSessionLabel());
    assertEquals("Desconhecido", plugin.unknownAuthorsLabel());

    assertTrue(server.tps().name().contains("TPS"));
    assertTrue(player.head().name().contains("{player}"));
    assertTrue(plugin.name().name().contains("{name}"));
  }

  private static boolean within(int slot, int max) {
    return slot >= 0 && slot < max;
  }
}
