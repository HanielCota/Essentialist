package com.hanielcota.essentials.modules.info.config;

import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Templates for every entry shown under the "Server" tab. Each entry exposes the runtime
 * placeholder(s) it accepts; the producer fills them in. Edit names/lores freely without touching
 * code.
 */
@ConfigSerializable
public record ServerEntriesSection(
    @Comment("TPS entry. Placeholder: {tps} (e.g. 19.95).") InfoEntryConfig tps,
    @Comment("Players entry. Placeholders: {online}, {max}.") InfoEntryConfig players,
    @Comment("Server software/version entry. Placeholder: {version}.") InfoEntryConfig version,
    @Comment("Uptime entry. Placeholder: {uptime} (formatted duration).") InfoEntryConfig uptime,
    @Comment("Memory entry. Placeholders: {usedMb}, {maxMb}.") InfoEntryConfig memory,
    @Comment("Loaded-worlds entry. Placeholder: {count}.") InfoEntryConfig worlds) {

  public static ServerEntriesSection defaults() {
    return new ServerEntriesSection(
        InfoEntryConfig.of(Material.CLOCK, "<yellow>TPS", "<gray>{tps}"),
        InfoEntryConfig.of(
            Material.PLAYER_HEAD,
            "<yellow>Players online",
            "<gray>{online} <dark_gray>/ <gray>{max}"),
        InfoEntryConfig.of(Material.NAME_TAG, "<yellow>Version", "<gray>{version}"),
        InfoEntryConfig.of(Material.COMPARATOR, "<yellow>Uptime", "<gray>{uptime}"),
        InfoEntryConfig.of(
            Material.REDSTONE, "<yellow>Memory", "<gray>{usedMb} MB <dark_gray>/ <gray>{maxMb} MB"),
        InfoEntryConfig.of(Material.GRASS_BLOCK, "<yellow>Worlds", "<gray>{count} loaded"));
  }
}
