package com.hanielcota.essentials.modules.info.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record InfoConfig(@Comment("Title of the /info menu.") String menuTitle) {

  public static InfoConfig defaults() {
    return new InfoConfig("<dark_gray>Informações");
  }
}
