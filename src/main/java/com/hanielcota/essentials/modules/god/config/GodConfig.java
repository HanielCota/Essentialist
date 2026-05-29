package com.hanielcota.essentials.modules.god.config;

import com.hanielcota.essentials.config.MessagePair;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record GodConfig(
    @Comment("Shown to the player when god mode is enabled.") String enabled,
    @Comment("Placeholders: {player}.") String enabledOther,
    @Comment("Shown to the player when god mode is disabled.") String disabled,
    @Comment("Placeholders: {player}.") String disabledOther) {

  public static GodConfig defaults() {
    return new GodConfig(
        "<green>God mode enabled.",
        "<green>Enabled god mode for <gold>{player}</gold>.",
        "<red>God mode disabled.",
        "<red>Disabled god mode for <gold>{player}</gold>.");
  }

  public MessagePair toggle(boolean godEnabled) {
    return godEnabled
        ? new MessagePair(enabled, enabledOther)
        : new MessagePair(disabled, disabledOther);
  }
}
