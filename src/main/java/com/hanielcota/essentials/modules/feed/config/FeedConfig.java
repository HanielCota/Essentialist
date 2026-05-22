package com.hanielcota.essentials.modules.feed.config;

import com.hanielcota.essentials.config.MessagePair;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record FeedConfig(
    @Comment("Shown to the player when their hunger is restored.") String fed,
    @Comment("Placeholders: {player}.") String fedOther,
    @Comment("Shown when the player is already full.") String alreadyFull,
    @Comment("Placeholders: {player}.") String alreadyFullOther,
    @Comment("Shown after /alimentar todos. Placeholders: {count}.") String fedAll) {

  public static FeedConfig defaults() {
    return new FeedConfig(
        "<green>Fome restaurada.",
        "<green>Alimentou <gold>{player}</gold>.",
        "<red>Sua fome já está cheia.",
        "<red>A fome de <gold>{player}</gold> já está cheia.",
        "<green><gold>{count}</gold> jogador(es) alimentado(s).");
  }

  public MessagePair whenFed() {
    return new MessagePair(fed, fedOther);
  }

  public MessagePair whenAlreadyFull() {
    return new MessagePair(alreadyFull, alreadyFullOther);
  }

  public String formatFedAll(int count) {
    return fedAll.replace("{count}", Integer.toString(count));
  }
}
