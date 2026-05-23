package com.hanielcota.essentials.modules.info.service;

import java.util.List;
import java.util.UUID;
import org.bukkit.Material;
import org.jspecify.annotations.Nullable;

/**
 * One row of information shown in the /info menu: an icon, a name and lore lines. When {@code
 * headOwner} is set, the row is rendered as that player's head.
 */
public record InfoEntry(Material icon, String name, List<String> lore, @Nullable UUID headOwner) {

  public InfoEntry {
    lore = List.copyOf(lore);
  }

  public static InfoEntry of(Material icon, String name, String... lore) {
    return new InfoEntry(icon, name, List.of(lore), null);
  }

  /** An entry rendered as the player head of {@code owner}. */
  public static InfoEntry head(UUID owner, String name, String... lore) {
    return new InfoEntry(Material.PLAYER_HEAD, name, List.of(lore), owner);
  }
}
