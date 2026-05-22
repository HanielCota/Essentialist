package com.hanielcota.essentials.modules.invsee.config;

import java.util.Objects;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record InvseeConfig(
    @Comment("Shown when /invsee opens an inventory. Placeholder: {player}.") String opened,
    @Comment("Shown when a player runs /invsee on themselves.") String self) {

  public static InvseeConfig defaults() {
    return new InvseeConfig(
        "<green>Abrindo o inventário de <gold>{player}</gold>.",
        "<red>Você não pode ver o próprio inventário.");
  }

  public String formatOpened(String player) {
    Objects.requireNonNull(player, "player");
    return opened.replace("{player}", player);
  }
}
