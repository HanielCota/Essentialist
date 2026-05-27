package com.hanielcota.essentials.modules.info.menu.presentation;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.info.config.InfoConfig;
import com.hanielcota.essentials.modules.info.config.PluginEntriesSection;
import java.util.List;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
public final class PluginInfoEntries {

  private final Plugin plugin;
  private final ConfigHandle<InfoConfig> config;

  private static String authors(
      @NonNull List<String> authors, @NonNull PluginEntriesSection section) {
    if (authors.isEmpty()) {
      return section.unknownAuthorsLabel();
    }

    return String.join(", ", authors);
  }

  public List<InfoEntry> entries() {
    var section = this.config.value().plugin();
    var meta = this.plugin.getPluginMeta();

    var name = meta.getName();
    var version = meta.getVersion();
    var authorsList = authors(meta.getAuthors(), section);
    var mcVersion = Bukkit.getMinecraftVersion();

    var nameEntry = InfoEntry.from(section.name(), Map.of("name", name, "version", version));
    var authorEntry = InfoEntry.from(section.authors(), Map.of("authors", authorsList));
    var mcEntry = InfoEntry.from(section.minecraft(), Map.of("minecraft", mcVersion));

    return List.of(nameEntry, authorEntry, mcEntry);
  }
}
