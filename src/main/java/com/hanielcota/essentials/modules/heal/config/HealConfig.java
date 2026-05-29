package com.hanielcota.essentials.modules.heal.config;

import com.hanielcota.essentials.config.MessagePair;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record HealConfig(
    @Comment("Shown to the player when their health is restored.") String healed,
    @Comment("Placeholders: {player}.") String healedOther,
    @Comment("Shown when the player already has full health.") String alreadyFull,
    @Comment("Placeholders: {player}.") String alreadyFullOther,
    @Comment("Shown when trying to heal a dead player.") String dead,
    @Comment("Placeholders: {player}.") String deadOther,
    @Comment("Shown after /heal all. Placeholders: {count}.") String healedAll) {

  public static HealConfig defaults() {
    return new HealConfig(
        "<green>Health restored.",
        "<green>Healed <gold>{player}</gold>.",
        "<red>Your health is already full.",
        "<red><gold>{player}</gold>'s health is already full.",
        "<red>You cannot heal yourself while you are dead.",
        "<red><gold>{player}</gold> is dead.",
        "<green>Restored the health of <gold>{count}</gold> player(s).");
  }

  public MessagePair whenHealed() {
    return new MessagePair(healed, healedOther);
  }

  public MessagePair whenAlreadyFull() {
    return new MessagePair(alreadyFull, alreadyFullOther);
  }

  public MessagePair whenDead() {
    return new MessagePair(dead, deadOther);
  }

  public String formatHealedAll(int count) {
    var countText = Integer.toString(count);
    return healedAll.replace("{count}", countText);
  }
}
