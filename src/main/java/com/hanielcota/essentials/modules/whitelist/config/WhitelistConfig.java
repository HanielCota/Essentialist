package com.hanielcota.essentials.modules.whitelist.config;

import java.util.List;
import java.util.Objects;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record WhitelistConfig(
    @Comment("Whitelist menu title.") String menuTitle,
    @Comment("Rows in the whitelist menu (1-6).") int menuRows,
    @Comment("Item name for each whitelisted player. Placeholder: {player}.") String itemName,
    @Comment("Item lore for each whitelisted player.") List<String> itemLore,
    @Comment("Shown after /whitelist add. Placeholder: {player}.") String added,
    @Comment("Shown when the player is already whitelisted. Placeholder: {player}.")
        String alreadyAdded,
    @Comment("Shown after a player is removed. Placeholder: {player}.") String removed,
    @Comment("Shown when the player is not whitelisted. Placeholder: {player}.")
        String notWhitelisted) {

  public static WhitelistConfig defaults() {
    return new WhitelistConfig(
        "<dark_gray>Whitelist",
        6,
        "<yellow>{player}",
        List.of("<gray>Clique para <red>remover</red> da whitelist."),
        "<green><gold>{player}</gold> foi adicionado à whitelist.",
        "<red><gold>{player}</gold> já está na whitelist.",
        "<green><gold>{player}</gold> foi removido da whitelist.",
        "<red><gold>{player}</gold> não está na whitelist.");
  }

  public String formatItemName(String player) {
    return withPlayer(itemName, player);
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

  private static String withPlayer(String template, String player) {
    Objects.requireNonNull(player, "player");
    return template.replace("{player}", player);
  }
}
