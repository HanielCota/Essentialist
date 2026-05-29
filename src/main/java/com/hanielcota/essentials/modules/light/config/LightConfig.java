package com.hanielcota.essentials.modules.light.config;

import com.hanielcota.essentials.config.MessagePair;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record LightConfig(
    @Comment("Shown to the player when night vision is enabled.") String enabled,
    @Comment("Placeholders: {player}.") String enabledOther,
    @Comment("Shown to the player when night vision is disabled.") String disabled,
    @Comment("Placeholders: {player}.") String disabledOther) {

  public static LightConfig defaults() {
    return new LightConfig(
        "<green>Night vision enabled.",
        "<green>Enabled night vision for <gold>{player}</gold>.",
        "<red>Night vision disabled.",
        "<red>Disabled night vision for <gold>{player}</gold>.");
  }

  public MessagePair toggle(boolean lightEnabled) {
    return lightEnabled
        ? new MessagePair(enabled, enabledOther)
        : new MessagePair(disabled, disabledOther);
  }
}
