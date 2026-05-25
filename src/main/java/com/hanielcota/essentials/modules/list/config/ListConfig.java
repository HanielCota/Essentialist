package com.hanielcota.essentials.modules.list.config;

import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.NavigationButtonsConfig;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record ListConfig(
    @Comment("List menu title.") String menuTitle,
    @Comment("Rows in the list menu (clamped to 1-6).") int menuRows,
    @Comment("Slots used by player entries. Leave empty to use every row except the last.")
        List<Integer> menuContentSlots,
    @Comment("Previous/next page buttons.") NavigationButtonsConfig navigation,
    @Comment("Slot of the static info item shown on every page.") int infoSlot,
    @Comment("Material of the static info item.") Material infoMaterial,
    @Comment("Name of the static info item.") String infoName,
    @Comment("Lore of the static info item — explain the legend.") List<String> infoLore,
    @Comment("Item name for each player entry. Placeholders: {player}, {group}.") String itemName,
    @Comment("Item lore for each player entry. Placeholders: {player}, {group}.")
        List<String> itemLore,
    @Comment("Material of the empty-state placeholder.") Material emptyMaterial,
    @Comment("Name of the empty-state placeholder.") String emptyName,
    @Comment("Lore of the empty-state placeholder.") List<String> emptyLore,
    @Comment("Groups in descending priority order. First match wins.") List<GroupDefinition> groups,
    @Comment("Fallback group for players that don't match any entry above.")
        DefaultGroup defaultGroup,
    @Comment("Shown when the console runs /list, since the menu needs a player.")
        String menuPlayerOnly) {

  private static final int MIN_ROWS = 1;

  public static ListConfig defaults() {
    return new ListConfig(
        "<dark_gray>Jogadores online",
        6,
        List.of(),
        NavigationButtonsConfig.defaults(48, 50),
        49,
        Material.BOOK,
        "<yellow>Legenda",
        List.of(
            "<gray>Jogadores agrupados por grupo de permissão.",
            "<gray>Maior prioridade aparece primeiro.",
            "",
            "<gold>Admin <gray>— essentials.list.group.admin",
            "<aqua>VIP <gray>— essentials.list.group.vip",
            "<gray>Membro <gray>— padrão"),
        "<yellow>{player}",
        List.of("<gray>Grupo: {group}"),
        Material.BARRIER,
        "<red>Nenhum jogador online",
        List.of("<gray>Não há jogadores visíveis no momento."),
        List.of(
            new GroupDefinition(
                "admin", "<gold>Admin", "essentials.list.group.admin", Material.GOLDEN_HELMET, 100),
            new GroupDefinition(
                "vip", "<aqua>VIP", "essentials.list.group.vip", Material.DIAMOND_HELMET, 50)),
        new DefaultGroup("<gray>Membro", Material.PLAYER_HEAD),
        "<red>O menu da lista só pode ser aberto por jogadores.");
  }

  public int effectiveRows() {
    return MenuLayouts.clampRows(menuRows);
  }

  public List<Integer> effectiveContentSlots() {
    if (menuContentSlots.isEmpty()) {
      var rows = effectiveRows();
      var count = rows > MIN_ROWS ? (rows - 1) * 9 : 9;

      return MenuLayouts.fallbackContentSlots(rows, count);
    }

    var rows = effectiveRows();

    return MenuLayouts.sanitizeSlots(menuContentSlots, rows);
  }

  public int effectiveInfoSlot() {
    var rows = effectiveRows();

    return MenuLayouts.sanitizeSlot(infoSlot, rows, (rows - 1) * 9 + 4);
  }

  public String formatItemName(@NonNull String player, @NonNull String group) {
    var withPlayer = itemName.replace("{player}", player);

    return withPlayer.replace("{group}", group);
  }

  public List<String> formatItemLore(@NonNull String player, @NonNull String group) {
    var formatted = new ArrayList<String>(itemLore.size());

    for (var line : itemLore) {
      var withPlayer = line.replace("{player}", player);
      var resolved = withPlayer.replace("{group}", group);
      formatted.add(resolved);
    }

    return List.copyOf(formatted);
  }
}
