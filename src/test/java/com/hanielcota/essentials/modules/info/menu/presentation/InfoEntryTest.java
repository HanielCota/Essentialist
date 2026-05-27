package com.hanielcota.essentials.modules.info.menu.presentation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.hanielcota.essentials.modules.info.config.InfoEntryConfig;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Material;
import org.junit.jupiter.api.Test;

class InfoEntryTest {

  @Test
  void fromExpandsNameAndLoreTokensAgainstValues() {
    var template =
        InfoEntryConfig.of(
            Material.CLOCK, "<yellow>TPS {tps}", "<gray>online <white>{online}/{max}");
    var values = Map.of("tps", "19.95", "online", 12, "max", 50);

    var entry = InfoEntry.from(template, values);

    assertEquals(Material.CLOCK, entry.icon());
    assertEquals("<yellow>TPS 19.95", entry.name());
    assertEquals("<gray>online <white>12/50", entry.lore().get(0));
    assertNull(entry.headOwner());
  }

  @Test
  void headStaticFactoryForcesPlayerHeadMaterialAndStoresOwner() {
    var owner = UUID.fromString("00000000-0000-0000-0000-000000000001");

    var entry = InfoEntry.head(owner, "<yellow>Steve", "<gray>id <white>" + owner);

    assertEquals(Material.PLAYER_HEAD, entry.icon());
    assertEquals("<yellow>Steve", entry.name());
    assertEquals(owner, entry.headOwner());
    assertNull(entry.headTexture());
  }

  @Test
  void headTextureSlotTakesPrecedenceWhenBothSet() {
    var owner = UUID.fromString("00000000-0000-0000-0000-000000000002");
    var texture = "base64-encoded-texture";

    var entry = new InfoEntry(Material.PLAYER_HEAD, "name", List.of("lore"), owner, texture);

    assertEquals(owner, entry.headOwner());
    assertEquals(texture, entry.headTexture());
  }

  @Test
  void unmatchedTokensRemainLiteralInOutput() {
    var template = InfoEntryConfig.of(Material.STONE, "no token", "<gray>{unknown}");
    var entry = InfoEntry.from(template, Map.of("other", "x"));

    assertEquals("no token", entry.name());
    assertEquals("<gray>{unknown}", entry.lore().get(0));
  }
}
