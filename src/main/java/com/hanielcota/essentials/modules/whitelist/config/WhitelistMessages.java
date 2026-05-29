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
        "<green><gold>{player}</gold> was added to the whitelist.",
        "<red><gold>{player}</gold> is already on the whitelist.",
        "<green><gold>{player}</gold> was removed from the whitelist.",
        "<red><gold>{player}</gold> is not on the whitelist.",
        "<red><gold>{player}</gold> has never joined the server.",
        "<red>The whitelist menu can only be opened by players.");
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
