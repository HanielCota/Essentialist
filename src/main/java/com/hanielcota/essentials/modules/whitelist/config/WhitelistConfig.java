package com.hanielcota.essentials.modules.whitelist.config;

import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.menu.NavigationButtonsConfig;
import java.util.List;
import lombok.NonNull;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record WhitelistConfig(
    @Comment("Whitelist menu title.") String menuTitle,
    @Comment("Rows in the whitelist menu (clamped to 1-6).") int menuRows,
    @Comment("Slots used by whitelist entries. Leave empty to use every row except the last.")
        List<Integer> menuContentSlots,
    @Comment("Previous/next page buttons.") NavigationButtonsConfig navigation,
    @Comment("Slot of the static info item shown on every page.") int infoSlot,
    @Comment("Material of the static info item.") Material infoMaterial,
    @Comment("Name of the static info item.") String infoName,
    @Comment("Lore of the static info item — explain /whitelist usage.") List<String> infoLore,
    @Comment("Item name for each whitelisted player. Placeholder: {player}.") String itemName,
    @Comment("Item lore for each whitelisted player. Placeholder: {player}.") List<String> itemLore,
    @Comment("Material of the placeholder shown when the whitelist is empty.")
        Material emptyMaterial,
    @Comment("Name of the placeholder shown when the whitelist is empty.") String emptyName,
    @Comment("Lore of the placeholder shown when the whitelist is empty.") List<String> emptyLore,
    @Comment("Shown after /whitelist add. Placeholder: {player}.") String added,
    @Comment("Shown when the player is already whitelisted. Placeholder: {player}.")
        String alreadyAdded,
    @Comment("Shown after a player is removed. Placeholder: {player}.") String removed,
    @Comment("Shown when the player is not whitelisted. Placeholder: {player}.")
        String notWhitelisted,
    @Comment("Shown when /whitelist add gets an unknown name. Placeholder: {player}.")
        String unknownPlayer,
    @Comment("Shown when the console runs /whitelist, since the menu needs a player.")
        String menuPlayerOnly) {

  private static final int MIN_ROWS = 1;

  public static WhitelistConfig defaults() {
    return new WhitelistConfig(
        "<dark_gray>Whitelist",
        6,
        List.of(
            11, 12, 13, 14, 15, 16, 18, 19, 20, 21, 22, 23, 24, 25, 27, 28, 29, 30, 31, 32, 33, 34,
            36, 37, 38, 39, 40, 41, 42, 43),
        NavigationButtonsConfig.defaults(48, 50),
        10,
        Material.BOOK,
        "<yellow>Whitelist do servidor",
        List.of(
            "<gray>Só jogadores adicionados podem entrar.",
            "",
            "<yellow>/whitelist add [jogador] <gray>adiciona",
            "<yellow>/whitelist remove [jogador] <gray>remove",
            "<yellow>/whitelist <gray>abre este menu",
            "",
            "<gray>Aqui no menu:",
            "<yellow>Clique <gray>remove o jogador"),
        "<yellow>{player}",
        List.of("<gray>Clique para <red>remover</red> da whitelist."),
        Material.BARRIER,
        "<red>A whitelist está vazia",
        List.of(
            "<gray>Nenhum jogador foi adicionado ainda.",
            "",
            "<yellow>/whitelist add [jogador]",
            "<dark_gray>» <gray>adiciona um jogador à whitelist",
            "",
            "<yellow>/whitelist remove [jogador]",
            "<dark_gray>» <gray>remove um jogador da whitelist"),
        "<green><gold>{player}</gold> foi adicionado à whitelist.",
        "<red><gold>{player}</gold> já está na whitelist.",
        "<green><gold>{player}</gold> foi removido da whitelist.",
        "<red><gold>{player}</gold> não está na whitelist.",
        "<red><gold>{player}</gold> nunca entrou no servidor.",
        "<red>O menu da whitelist só pode ser aberto por jogadores.");
  }

  private static String withPlayer(@NonNull String template, @NonNull String player) {
    return template.replace("{player}", player);
  }

  /** Configured menu rows clamped to the supported 1-6 range. */
  public int effectiveRows() {
    return MenuLayouts.clampRows(menuRows);
  }

  public List<Integer> effectiveContentSlots() {
    if (menuContentSlots.isEmpty()) {
      var rows = effectiveRows();
      var count = rows > MIN_ROWS ? (rows - 1) * 9 : 9;
      return MenuLayouts.fallbackContentSlots(rows, count);
    }
    return MenuLayouts.sanitizeSlots(menuContentSlots, effectiveRows());
  }

  public int effectiveInfoSlot() {
    return MenuLayouts.sanitizeSlot(infoSlot, effectiveRows(), 10);
  }

  public String formatItemName(@NonNull String player) {
    return withPlayer(itemName, player);
  }

  /** Item lore with {@code {player}} resolved on every line. */
  public List<String> formatLore(@NonNull String player) {
    var replaced = itemLore.stream().map(line -> line.replace("{player}", player));

    return replaced.toList();
  }

  public String formatAdded(@NonNull String player) {
    return withPlayer(added, player);
  }

  public String formatAlreadyAdded(@NonNull String player) {
    return withPlayer(alreadyAdded, player);
  }

  public String formatRemoved(@NonNull String player) {
    return withPlayer(removed, player);
  }

  public String formatNotWhitelisted(@NonNull String player) {
    return withPlayer(notWhitelisted, player);
  }

  public String formatUnknownPlayer(@NonNull String player) {
    return withPlayer(unknownPlayer, player);
  }
}
