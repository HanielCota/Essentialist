package com.hanielcota.essentials.modules.kill.config;

import com.hanielcota.essentials.config.MessagePair;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record KillConfig(
    @Comment("Shown to the player when they are killed.") String killed,
    @Comment("Placeholders: {player}.") String killedOther,
    @Comment("Shown when the target is already dead.") String alreadyDead,
    @Comment("Placeholders: {player}.") String alreadyDeadOther) {

  public static KillConfig defaults() {
    return new KillConfig(
        "<red>Você foi morto.",
        "<red>Você matou <gold>{player}</gold>.",
        "<red>Você já está morto.",
        "<red><gold>{player}</gold> já está morto.");
  }

  public MessagePair whenKilled() {
    return new MessagePair(killed, killedOther);
  }

  public MessagePair whenAlreadyDead() {
    return new MessagePair(alreadyDead, alreadyDeadOther);
  }
}
