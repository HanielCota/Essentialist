package com.hanielcota.essentials.modules.info.config;

import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** Templates for every entry shown under the "Essentialist" / about tab. */
@ConfigSerializable
public record PluginEntriesSection(
    @Comment("Plugin name entry. Placeholders: {name}, {version}.") InfoEntryConfig name,
    @Comment("Plugin authors entry. Placeholder: {authors}.") InfoEntryConfig authors,
    @Comment("Server Minecraft version entry. Placeholder: {minecraft}.") InfoEntryConfig minecraft,
    @Comment("Label substituted into {authors} when the plugin.yml authors list is empty.")
        String unknownAuthorsLabel) {

  public static PluginEntriesSection defaults() {
    return new PluginEntriesSection(
        InfoEntryConfig.of(Material.NETHER_STAR, "<yellow>{name}", "<gray>Versão <white>{version}"),
        InfoEntryConfig.of(Material.WRITABLE_BOOK, "<yellow>Autor", "<gray>{authors}"),
        InfoEntryConfig.of(Material.GRASS_BLOCK, "<yellow>Minecraft", "<gray>{minecraft}"),
        "Desconhecido");
  }
}
