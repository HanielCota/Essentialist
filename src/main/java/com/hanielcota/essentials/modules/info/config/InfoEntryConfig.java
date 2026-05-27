package com.hanielcota.essentials.modules.info.config;

import java.util.List;
import lombok.NonNull;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * One slot rendered inside an /info detail tab: an icon, a MiniMessage name template, and a list of
 * MiniMessage lore templates. The producer fills in any {@code {placeholder}} tokens at render
 * time.
 */
@ConfigSerializable
public record InfoEntryConfig(
    @Comment("Icon material.") Material icon,
    @Comment("Display name (MiniMessage). Placeholders depend on the entry.") String name,
    @Comment("Lore lines (MiniMessage). Placeholders depend on the entry.") List<String> lore) {

  public static InfoEntryConfig of(
      @NonNull Material icon, @NonNull String name, @NonNull String... lore) {
    return new InfoEntryConfig(icon, name, List.of(lore));
  }
}
