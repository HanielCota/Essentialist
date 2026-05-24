package com.hanielcota.essentials.modules.info.presentation;

import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public final class PluginInfoEntries {

  private static final String GRAY = "<gray>";

  private final Plugin plugin;

  private static String authors(@NonNull List<String> authors) {
    if (authors.isEmpty()) {
      return "Desconhecido";
    }
    return String.join(", ", authors);
  }

  public List<InfoEntry> entries() {
    var meta = this.plugin.getPluginMeta();
    var name = meta.getName();
    var version = meta.getVersion();
    var authorsList = authors(meta.getAuthors());
    var mcVersion = Bukkit.getMinecraftVersion();

    return List.of(
        InfoEntry.of(Material.NETHER_STAR, "<yellow>" + name, "<gray>Versão <white>" + version),
        InfoEntry.of(Material.WRITABLE_BOOK, "<yellow>Autor", GRAY + authorsList),
        InfoEntry.of(Material.GRASS_BLOCK, "<yellow>Minecraft", GRAY + mcVersion));
  }
}
