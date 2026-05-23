package com.hanielcota.essentials.modules.homes.config;

import com.hanielcota.essentials.util.Numbers;
import java.util.List;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

/** Visual + layout settings for /homes and the material picker submenu. */
@ConfigSerializable
public record HomesMenuConfig(
    @Comment("/homes menu title. Placeholders: {count}, {limit}.") String title,
    @Comment("/homes menu rows (1-6).") int rows,
    @Comment("/homes item name. Placeholders: {name}.") String itemName,
    @Comment(
            "/homes item lore. Placeholders: {world}, {x}, {y}, {z}. "
                + "Tip: hint the click actions here.")
        List<String> itemLore,
    @Comment("Add an enchant glow to the /homes items.") boolean itemGlow,
    @Comment("Material picker submenu title. Placeholders: {name}.") String pickerTitle,
    @Comment("Material picker rows (1-6).") int pickerRows,
    @Comment("Materials offered in the picker submenu.") List<Material> palette) {

  public static HomesMenuConfig defaults() {
    return new HomesMenuConfig(
        "<dark_gray>Suas homes <gray>(<gold>{count}</gold>/<gold>{limit}</gold>)",
        6,
        "<gold>{name}",
        List.of(
            "<gray>Mundo: <white>{world}",
            "<gray>Coordenadas: <white>{x}, {y}, {z}",
            "",
            "<yellow>Clique esquerdo <gray>para teleportar",
            "<yellow>Clique direito <gray>para deletar",
            "<yellow>Shift + clique <gray>para renomear",
            "<yellow>Q (drop) <gray>para trocar o ícone"),
        false,
        "<dark_gray>Ícone de <gold>{name}",
        6,
        List.of(
            Material.RED_BED,
            Material.WHITE_BED,
            Material.BLUE_BED,
            Material.LIME_BED,
            Material.YELLOW_BED,
            Material.PURPLE_BED,
            Material.COMPASS,
            Material.RECOVERY_COMPASS,
            Material.MAP,
            Material.FILLED_MAP,
            Material.ENDER_CHEST,
            Material.CHEST,
            Material.BARREL,
            Material.SHULKER_BOX,
            Material.NETHER_STAR,
            Material.BEACON,
            Material.DRAGON_EGG,
            Material.TOTEM_OF_UNDYING,
            Material.LANTERN,
            Material.SOUL_LANTERN,
            Material.TORCH,
            Material.REDSTONE_TORCH,
            Material.BOOK,
            Material.WRITABLE_BOOK,
            Material.KNOWLEDGE_BOOK,
            Material.NAME_TAG,
            Material.PAPER,
            Material.EMERALD,
            Material.DIAMOND,
            Material.NETHERITE_INGOT,
            Material.GOLD_INGOT,
            Material.IRON_INGOT,
            Material.OAK_SAPLING,
            Material.GRASS_BLOCK,
            Material.DIRT,
            Material.STONE,
            Material.OBSIDIAN,
            Material.CRYING_OBSIDIAN,
            Material.END_PORTAL_FRAME,
            Material.NETHER_PORTAL,
            Material.CAKE,
            Material.JUKEBOX,
            Material.NOTE_BLOCK,
            Material.CRAFTING_TABLE,
            Material.FURNACE,
            Material.ANVIL,
            Material.ENCHANTING_TABLE,
            Material.BREWING_STAND,
            Material.CAULDRON,
            Material.SHIELD));
  }

  public String formatTitle(int count, int limit) {
    return title
        .replace("{count}", Integer.toString(count))
        .replace("{limit}", Integer.toString(limit));
  }

  public String formatItemName(String name) {
    return itemName.replace("{name}", name);
  }

  public String[] renderItemLore(String world, double x, double y, double z) {
    var xStr = Numbers.compact(x);
    var yStr = Numbers.compact(y);
    var zStr = Numbers.compact(z);
    var rendered = new String[itemLore.size()];

    for (var i = 0; i < itemLore.size(); i++) {
      rendered[i] =
          itemLore
              .get(i)
              .replace("{world}", world)
              .replace("{x}", xStr)
              .replace("{y}", yStr)
              .replace("{z}", zStr);
    }
    return rendered;
  }

  public String formatPickerTitle(String homeName) {
    return pickerTitle.replace("{name}", homeName);
  }
}
