package com.hanielcota.essentials.modules.compact.config;

import java.util.Map;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record CompactConfig(
    @Comment("Compaction mappings: ingredient → { block, amount }.") Map<Material, Recipe> recipes,
    @Comment("Shown when /compactar finds nothing to compact.") String nothing,
    @Comment("Shown after /compactar. Placeholders: {count}.") String success) {

  public static CompactConfig defaults() {
    return new CompactConfig(
        defaultRecipes(),
        "<red>Você não tem nada para compactar.",
        "<green>Compactado <gold>{count}</gold> bloco(s).");
  }

  private static Map<Material, Recipe> defaultRecipes() {
    return Map.ofEntries(
        Map.entry(Material.IRON_INGOT, new Recipe(Material.IRON_BLOCK, 9)),
        Map.entry(Material.GOLD_INGOT, new Recipe(Material.GOLD_BLOCK, 9)),
        Map.entry(Material.COPPER_INGOT, new Recipe(Material.COPPER_BLOCK, 9)),
        Map.entry(Material.DIAMOND, new Recipe(Material.DIAMOND_BLOCK, 9)),
        Map.entry(Material.EMERALD, new Recipe(Material.EMERALD_BLOCK, 9)),
        Map.entry(Material.NETHERITE_INGOT, new Recipe(Material.NETHERITE_BLOCK, 9)),
        Map.entry(Material.REDSTONE, new Recipe(Material.REDSTONE_BLOCK, 9)),
        Map.entry(Material.LAPIS_LAZULI, new Recipe(Material.LAPIS_BLOCK, 9)),
        Map.entry(Material.COAL, new Recipe(Material.COAL_BLOCK, 9)),
        Map.entry(Material.RAW_IRON, new Recipe(Material.RAW_IRON_BLOCK, 9)),
        Map.entry(Material.RAW_GOLD, new Recipe(Material.RAW_GOLD_BLOCK, 9)),
        Map.entry(Material.RAW_COPPER, new Recipe(Material.RAW_COPPER_BLOCK, 9)),
        Map.entry(Material.WHEAT, new Recipe(Material.HAY_BLOCK, 9)),
        Map.entry(Material.SLIME_BALL, new Recipe(Material.SLIME_BLOCK, 9)),
        Map.entry(Material.AMETHYST_SHARD, new Recipe(Material.AMETHYST_BLOCK, 4)));
  }

  public String formatSuccess(int count) {
    return success.replace("{count}", Integer.toString(count));
  }

  @ConfigSerializable
  public record Recipe(Material block, int amount) {}
}
