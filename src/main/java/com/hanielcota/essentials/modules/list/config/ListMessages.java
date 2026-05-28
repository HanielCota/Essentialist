package com.hanielcota.essentials.modules.list.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record ListMessages(
    @Comment("Shown when the console runs /list, since the menu needs a player.")
        String menuPlayerOnly) {

  public static ListMessages defaults() {
    return new ListMessages("<red>O menu da lista só pode ser aberto por jogadores.");
  }
}
