package com.hanielcota.essentials.modules.kit.config;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Persisted definition of a single kit (one entry in {@code kits.yml}). The metadata is editable by
 * hand; {@code items} is captured by {@code /kit create} as Base64-encoded ItemStacks.
 */
@ConfigSerializable
public record KitDefinitionConfig(
    @Comment("Display name shown in the menus (MiniMessage).") String displayName,
    @Comment("Icon shown for this kit in the list menu.") Material icon,
    @Comment("Category id this kit belongs to (see the categories section in kit.yml).")
        String category,
    @Comment("Cooldown between claims, in seconds. 0 disables the cooldown.") long cooldownSeconds,
    @Comment("When true, the kit can be claimed only once per player, ever.") boolean oneTime,
    @Comment("Permission required to see and claim the kit. Empty means everyone.")
        String permission,
    @Comment("When true, the kit is given automatically the first time a player joins.")
        boolean firstJoin,
    @Comment("Serialized kit items (Base64). Managed by /kit create — edit with care.")
        List<String> items) {

  public static KitDefinitionConfig of(
      String displayName,
      Material icon,
      String category,
      long cooldownSeconds,
      boolean oneTime,
      String permission,
      boolean firstJoin,
      List<String> items) {
    return new KitDefinitionConfig(
        displayName, icon, category, cooldownSeconds, oneTime, permission, firstJoin, items);
  }

  /**
   * Copy of this definition with replaced items (used when /kit create overwrites an existing kit).
   */
  public KitDefinitionConfig withItems(List<String> newItems) {
    return new KitDefinitionConfig(
        displayName, icon, category, cooldownSeconds, oneTime, permission, firstJoin, newItems);
  }
}
