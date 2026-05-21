package com.hanielcota.essentials.modules.fly.config;

import com.hanielcota.essentials.config.MessagePair;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record FlyConfig(
    @Comment("Shown to the player when their flight is enabled.") String enabled,
    @Comment("Placeholders: {player}.") String enabledOther,
    @Comment("Shown to the player when their flight is disabled.") String disabled,
    @Comment("Placeholders: {player}.") String disabledOther) {

  public static FlyConfig defaults() {
    return new FlyConfig(
        "<green>Flight enabled.",
        "<green>Enabled flight for <gold>{player}</gold>.",
        "<red>Flight disabled.",
        "<red>Disabled flight for <gold>{player}</gold>.");
  }

  public MessagePair toggle(boolean flightEnabled) {
    return flightEnabled
        ? new MessagePair(enabled, enabledOther)
        : new MessagePair(disabled, disabledOther);
  }
}
