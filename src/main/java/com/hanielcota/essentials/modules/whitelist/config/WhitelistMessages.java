package com.hanielcota.essentials.modules.whitelist.config;

import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record WhitelistMessages(
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

  public static WhitelistMessages defaults() {
    return new WhitelistMessages(
        "<green><gold>{player}</gold> foi adicionado à whitelist.",
        "<red><gold>{player}</gold> já está na whitelist.",
        "<green><gold>{player}</gold> foi removido da whitelist.",
        "<red><gold>{player}</gold> não está na whitelist.",
        "<red><gold>{player}</gold> nunca entrou no servidor.",
        "<red>O menu da whitelist só pode ser aberto por jogadores.");
  }

  public String formatAdded(@NonNull String player) {
    return added.replace("{player}", player);
  }

  public String formatAlreadyAdded(@NonNull String player) {
    return alreadyAdded.replace("{player}", player);
  }

  public String formatRemoved(@NonNull String player) {
    return removed.replace("{player}", player);
  }

  public String formatNotWhitelisted(@NonNull String player) {
    return notWhitelisted.replace("{player}", player);
  }

  public String formatUnknownPlayer(@NonNull String player) {
    return unknownPlayer.replace("{player}", player);
  }
}
