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
    var template = templateFor(count);

    // Full-server-bypass players can push the count past max — never display more than max.
    var shown = Math.min(count, max);
    var shownStr = Integer.toString(shown);
    var maxStr = Integer.toString(max);

    var withCount = template.replace("{count}", shownStr);
    return withCount.replace("{max}", maxStr);
  }

  private String templateFor(int count) {
    if (count == 0) {
      return empty;
    }
    if (count == 1) {
      return singular;
    }
    return plural;
  }
}
