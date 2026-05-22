package com.hanielcota.essentials.modules.info.service;

import java.util.List;
import java.util.Objects;
import org.bukkit.Material;

/** One row of information shown in an /info category menu: an icon, a name and lore lines. */
public record InfoEntry(Material icon, String name, List<String> lore) {

  public InfoEntry {
    Objects.requireNonNull(icon, "icon");
    Objects.requireNonNull(name, "name");
    lore = List.copyOf(lore);
  }

  public static InfoEntry of(Material icon, String name, String... lore) {
    return new InfoEntry(icon, name, List.of(lore));
  }
}
