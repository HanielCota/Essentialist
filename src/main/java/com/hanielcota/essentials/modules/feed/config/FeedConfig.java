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
    @Comment("Shown after /feed all. Placeholders: {count}.") String fedAll) {

  public static FeedConfig defaults() {
    return new FeedConfig(
        "<green>Food restored.",
        "<green>Fed <gold>{player}</gold>.",
        "<red>Your food is already full.",
        "<red><gold>{player}</gold>'s food is already full.",
        "<green><gold>{count}</gold> player(s) fed.");
  }

  public MessagePair whenFed() {
    return new MessagePair(fed, fedOther);
  }

  public MessagePair whenAlreadyFull() {
    return new MessagePair(alreadyFull, alreadyFullOther);
  }

  public String formatFedAll(int count) {
    var countText = Integer.toString(count);
    return fedAll.replace("{count}", countText);
  }
}
