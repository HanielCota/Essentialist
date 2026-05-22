package com.hanielcota.essentials.modules.fly.config;

import com.hanielcota.essentials.config.MessagePair;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record FlyConfig(
    @Comment("Shown to the player when their flight is enabled.") String enabled,
    @Comment("Placeholders: {player}.") String enabledOther,
    @Comment("Shown to the player when their flight is disabled.") String disabled,
    @Comment("Placeholders: {player}.") String disabledOther,
    @Comment("Shown when /fly is used in Creative or Spectator mode.") String unsupported,
    @Comment("Placeholders: {player}.") String unsupportedOther) {

  public static FlyConfig defaults() {
    return new FlyConfig(
        "<green>Flight enabled.",
        "<green>Enabled flight for <gold>{player}</gold>.",
        "<red>Flight disabled.",
        "<red>Disabled flight for <gold>{player}</gold>.",
        "<red>O modo de jogo atual já permite voar.",
        "<red>O modo de jogo de <gold>{player}</gold> já permite voar.");
  }

  public MessagePair toggle(boolean flightEnabled) {
    return flightEnabled
        ? new MessagePair(enabled, enabledOther)
        : new MessagePair(disabled, disabledOther);
  }

  public MessagePair unsupportedGamemode() {
    return new MessagePair(unsupported, unsupportedOther);
  }
}
