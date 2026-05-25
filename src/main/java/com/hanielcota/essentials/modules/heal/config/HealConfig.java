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
    @Comment("Shown after /curar todos. Placeholders: {count}.") String healedAll) {

  public static HealConfig defaults() {
    return new HealConfig(
        "<green>Vida restaurada.",
        "<green>Curou <gold>{player}</gold>.",
        "<red>Sua vida já está cheia.",
        "<red>A vida de <gold>{player}</gold> já está cheia.",
        "<red>Você não pode se curar enquanto está morto.",
        "<red><gold>{player}</gold> está morto.",
        "<green>Vida de <gold>{count}</gold> jogador(es) restaurada.");
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
