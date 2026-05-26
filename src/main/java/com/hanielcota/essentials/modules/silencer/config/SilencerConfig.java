package com.hanielcota.essentials.modules.silencer.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record SilencerConfig(
    @Comment("Suppress the vanilla join message broadcast to every player.") boolean suppressJoin,
    @Comment("Suppress the vanilla quit message broadcast to every player.") boolean suppressQuit,
    @Comment("Suppress the vanilla death message broadcast to every player.") boolean suppressDeath,
    @Comment("Suppress the vanilla advancement broadcast (\"X has made the advancement Y\").")
        boolean suppressAdvancement) {

  public static SilencerConfig defaults() {
    return new SilencerConfig(true, true, true, true);
  }
}
