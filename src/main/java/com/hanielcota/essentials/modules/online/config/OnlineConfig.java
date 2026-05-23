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
    // Full-server-bypass players can push the count past max — never display more than max.
    int shown = Math.min(count, max);
    var shownStr = Integer.toString(shown);
    var maxStr = Integer.toString(max);

    return template.replace("{count}", shownStr).replace("{max}", maxStr);
  }
}
