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
    var rawAuthors = meta.getAuthors();
    var authorsList = authors(rawAuthors);
    var mcVersion = Bukkit.getMinecraftVersion();

    var nameTitle = "<yellow>" + name;
    var versionLore = "<gray>Versão <white>" + version;
    var authorLore = GRAY + authorsList;
    var mcLore = GRAY + mcVersion;

    var nameEntry = InfoEntry.of(Material.NETHER_STAR, nameTitle, versionLore);
    var authorEntry = InfoEntry.of(Material.WRITABLE_BOOK, "<yellow>Autor", authorLore);
    var mcEntry = InfoEntry.of(Material.GRASS_BLOCK, "<yellow>Minecraft", mcLore);

    return List.of(nameEntry, authorEntry, mcEntry);
  }
}
