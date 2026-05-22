package com.hanielcota.essentials.modules.whitelist.config;

import java.util.List;
import java.util.Objects;
import org.bukkit.Material;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record WhitelistConfig(
    @Comment("Whitelist menu title.") String menuTitle,
    @Comment("Rows in the whitelist menu (clamped to 1-6).") int menuRows,
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
  private static final int MAX_ROWS = 6;

  public static WhitelistConfig defaults() {
    return new WhitelistConfig(
        "<dark_gray>Whitelist",
        6,
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

  /** Configured menu rows clamped to the supported 1-6 range. */
  public int effectiveRows() {
    return Math.clamp(menuRows, MIN_ROWS, MAX_ROWS);
  }

  public String formatItemName(String player) {
    return withPlayer(itemName, player);
  }

  /** Item lore with {@code {player}} resolved on every line. */
  public List<String> formatLore(String player) {
    Objects.requireNonNull(player, "player");
    return itemLore.stream().map(line -> line.replace("{player}", player)).toList();
  }

  public String formatAdded(String player) {
    return withPlayer(added, player);
  }

  public String formatAlreadyAdded(String player) {
    return withPlayer(alreadyAdded, player);
  }

  public String formatRemoved(String player) {
    return withPlayer(removed, player);
  }

  public String formatNotWhitelisted(String player) {
    return withPlayer(notWhitelisted, player);
  }

  public String formatUnknownPlayer(String player) {
    return withPlayer(unknownPlayer, player);
  }

  private static String withPlayer(String template, String player) {
    Objects.requireNonNull(player, "player");
    return template.replace("{player}", player);
  }
}
