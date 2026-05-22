package com.hanielcota.essentials.modules.info.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record InfoConfig(
    @Comment("Title of the /info category menu.") String menuTitle,
    @Comment("Title of the server info menu.") String serverTitle,
    @Comment("Title of the player info menu.") String playerTitle,
    @Comment("Title of the Essentialist info menu.") String aboutTitle) {

  public static InfoConfig defaults() {
    return new InfoConfig(
        "<dark_gray>Informações",
        "<dark_gray>Informações » Servidor",
        "<dark_gray>Informações » Jogador",
        "<dark_gray>Informações » Essentialist");
  }
}
