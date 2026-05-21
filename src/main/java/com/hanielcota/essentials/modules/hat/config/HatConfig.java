package com.hanielcota.essentials.modules.hat.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record HatConfig(
    @Comment("Shown when a hat is equipped.") String equipped,
    @Comment("Shown when the player is not holding any item.") String emptyHand) {

  public static HatConfig defaults() {
    return new HatConfig(
        "<green>Chapéu equipado.",
        "<red>Você precisa estar segurando um item para usar como chapéu.");
  }
}
