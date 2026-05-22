package com.hanielcota.essentials.modules.online.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record OnlineConfig(
    @Comment("Shown by /online when nobody is online.") String empty,
    @Comment("Shown by /online when exactly one player is online. Placeholders: {count}, {max}.")
        String singular,
    @Comment("Shown by /online for two or more players. Placeholders: {count}, {max}.")
        String plural) {

  public static OnlineConfig defaults() {
    return new OnlineConfig(
        "<gray>Não há ninguém online no momento.",
        "<yellow>Há <gold>{count}</gold>/<gold>{max}</gold> jogador online.",
        "<yellow>Há <gold>{count}</gold>/<gold>{max}</gold> jogadores online.");
  }

  public String format(int count, int max) {
    String template = count == 0 ? empty : count == 1 ? singular : plural;
    return template
        .replace("{count}", Integer.toString(count))
        .replace("{max}", Integer.toString(max));
  }
}
