package com.hanielcota.essentials.modules.invsee.config;

import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record InvseeConfig(
    @Comment("/invsee menu title. Placeholder: {player}.") String menuTitle,
    @Comment("Shown when /invsee opens an inventory. Placeholder: {player}.") String opened,
    @Comment("Shown when a player runs /invsee on themselves.") String self) {

  public static InvseeConfig defaults() {
    return new InvseeConfig(
        "<dark_gray>Inventário de {player}",
        "<green>Abrindo o inventário de <gold>{player}</gold>.",
        "<red>Você não pode ver o próprio inventário.");
  }

  public String formatTitle(@NonNull String player) {
    return menuTitle.replace("{player}", player);
  }

  public String formatOpened(@NonNull String player) {
    return opened.replace("{player}", player);
  }
}
