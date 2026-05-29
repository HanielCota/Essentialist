package com.hanielcota.essentials.modules.smelt.config;

import java.util.Map;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record SmeltConfig(
    @Comment("Smelting mappings: input Material → output Material.")
        Map<Material, Material> mappings,
    @Comment("Shown when /derreter finds nothing to smelt.") String nothing,
    @Comment("Shown after /derreter. Placeholders: {count}.") String success) {

  public static SmeltConfig defaults() {
    return new SmeltConfig(
        defaultMappings(),
        "<red>You have nothing to smelt.",
        "<green>Smelted <gold>{count}</gold> item(s).");
  }

  private static Map<Material, Material> defaultMappings() {
    return Map.ofEntries(
        Map.entry(Material.RAW_IRON, Material.IRON_INGOT),
        Map.entry(Material.RAW_GOLD, Material.GOLD_INGOT),
        Map.entry(Material.RAW_COPPER, Material.COPPER_INGOT),
        Map.entry(Material.IRON_ORE, Material.IRON_INGOT),
        Map.entry(Material.GOLD_ORE, Material.GOLD_INGOT),
        Map.entry(Material.COPPER_ORE, Material.COPPER_INGOT),
        Map.entry(Material.DEEPSLATE_IRON_ORE, Material.IRON_INGOT),
        Map.entry(Material.DEEPSLATE_GOLD_ORE, Material.GOLD_INGOT),
        Map.entry(Material.DEEPSLATE_COPPER_ORE, Material.COPPER_INGOT),
        Map.entry(Material.NETHER_GOLD_ORE, Material.GOLD_INGOT),
        Map.entry(Material.ANCIENT_DEBRIS, Material.NETHERITE_SCRAP),
        Map.entry(Material.SAND, Material.GLASS),
        Map.entry(Material.RED_SAND, Material.RED_STAINED_GLASS),
        Map.entry(Material.COBBLESTONE, Material.STONE),
        Map.entry(Material.STONE, Material.SMOOTH_STONE));
  }

  public String formatSuccess(int count) {
    return success.replace("{count}", Integer.toString(count));
  }
}
