package com.hanielcota.essentials.modules.online.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record OnlineConfig(
    @Comment("Shown by /online. Placeholders: {count}, {max}.") String message) {

  public static OnlineConfig defaults() {
    return new OnlineConfig(
        "<yellow>Há <gold>{count}</gold>/<gold>{max}</gold> jogador(es) online.");
  }

  public String formatMessage(int count, int max) {
    return message
        .replace("{count}", Integer.toString(count))
        .replace("{max}", Integer.toString(max));
  }
}
