package com.hanielcota.essentials.modules.invsee.config;

import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record InvseeConfig(
    @Comment("/invsee menu title. Placeholder: {player}.") String menuTitle,
    @Comment("Shown when /invsee opens an inventory. Placeholder: {player}.") String opened,
    @Comment("Shown when a player runs /invsee on themselves.") String self,
    @Comment("Shown when /invsee is denied because another viewer already holds the target.")
        String alreadyViewed) {

  public static InvseeConfig defaults() {
    return new InvseeConfig(
        "<dark_gray>{player}'s inventory",
        "<green>Opening <gold>{player}</gold>'s inventory.",
        "<red>You cannot view your own inventory.",
        "<red>That player's inventory is already being viewed by another staff member.");
  }

  public String formatTitle(@NonNull String player) {
    return menuTitle.replace("{player}", player);
  }

  public String formatOpened(@NonNull String player) {
    return opened.replace("{player}", player);
  }
}
