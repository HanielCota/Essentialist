package com.hanielcota.essentials.modules.kit.config;

import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/**
 * Persisted definition of a single kit (one entry under {@code kits} in {@code kit.yml}). Metadata
 * is editable by hand or via {@code /kit set*}; the item sections are captured by {@code /kit
 * create} as Base64 ItemStacks.
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
    @Comment("Serialized main-inventory items (Base64). Managed by /kit create — edit with care.")
        List<String> items,
    @Comment(
            "Serialized armor, positional: boots, leggings, chestplate, helmet. Equipped on claim.")
        List<String> armor,
    @Comment("Serialized off-hand item (0 or 1 entry).") List<String> offhand,
    @Comment(
            "When true, the cooldown resets daily at the configured hour instead of being rolling.")
        boolean dailyReset) {

  public static KitDefinitionConfig of(
      String displayName,
      Material icon,
      String category,
      long cooldownSeconds,
      boolean oneTime,
      String permission,
      boolean firstJoin,
      List<String> items,
      List<String> armor,
      List<String> offhand) {
    return new KitDefinitionConfig(
        displayName,
        icon,
        category,
        cooldownSeconds,
        oneTime,
        permission,
        firstJoin,
        items,
        armor,
        offhand,
        false);
  }

  /** Copy with replaced item sections (used when /kit create overwrites an existing kit). */
  public KitDefinitionConfig withContents(
      List<String> newItems, List<String> newArmor, List<String> newOffhand) {
    return new KitDefinitionConfig(
        displayName,
        icon,
        category,
        cooldownSeconds,
        oneTime,
        permission,
        firstJoin,
        newItems,
        newArmor,
        newOffhand,
        dailyReset);
  }

  public KitDefinitionConfig withDisplayName(String value) {
    return new KitDefinitionConfig(
        value,
        icon,
        category,
        cooldownSeconds,
        oneTime,
        permission,
        firstJoin,
        items,
        armor,
        offhand,
        dailyReset);
  }

  public KitDefinitionConfig withIcon(Material value) {
    return new KitDefinitionConfig(
        displayName,
        value,
        category,
        cooldownSeconds,
        oneTime,
        permission,
        firstJoin,
        items,
        armor,
        offhand,
        dailyReset);
  }

  public KitDefinitionConfig withCategory(String value) {
    return new KitDefinitionConfig(
        displayName,
        icon,
        value,
        cooldownSeconds,
        oneTime,
        permission,
        firstJoin,
        items,
        armor,
        offhand,
        dailyReset);
  }

  public KitDefinitionConfig withCooldownSeconds(long value) {
    return new KitDefinitionConfig(
        displayName,
        icon,
        category,
        value,
        oneTime,
        permission,
        firstJoin,
        items,
        armor,
        offhand,
        dailyReset);
  }

  public KitDefinitionConfig withOneTime(boolean value) {
    return new KitDefinitionConfig(
        displayName,
        icon,
        category,
        cooldownSeconds,
        value,
        permission,
        firstJoin,
        items,
        armor,
        offhand,
        dailyReset);
  }

  public KitDefinitionConfig withPermission(String value) {
    return new KitDefinitionConfig(
        displayName,
        icon,
        category,
        cooldownSeconds,
        oneTime,
        value,
        firstJoin,
        items,
        armor,
        offhand,
        dailyReset);
  }

  public KitDefinitionConfig withDailyReset(boolean value) {
    return new KitDefinitionConfig(
        displayName,
        icon,
        category,
        cooldownSeconds,
        oneTime,
        permission,
        firstJoin,
        items,
        armor,
        offhand,
        value);
  }
}
