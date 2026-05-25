package com.hanielcota.essentials.modules.socialspy.config;

import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record SocialSpyConfig(
    @Comment("Spy line shown to active spies. Placeholders: {sender}, {target}, {message}.")
        String spyFormat,
    @Comment("Shown when the caller enables their own spy.") String enabled,
    @Comment("Shown when the caller disables their own spy.") String disabled,
    @Comment("Shown to the caller after toggling another player on. Placeholder: {player}.")
        String enabledOther,
    @Comment("Shown to the caller after toggling another player off. Placeholder: {player}.")
        String disabledOther) {

  public static SocialSpyConfig defaults() {
    return new SocialSpyConfig(
        "<gray>[<dark_purple>SPY<gray>] <gold>{sender}<gray> -> <gold>{target}<gray>:"
            + " <white>{message}",
        "<green>Social spy ativado.",
        "<red>Social spy desativado.",
        "<green>Social spy ativado para <gold>{player}</gold>.",
        "<red>Social spy desativado para <gold>{player}</gold>.");
  }

  public String formatSpy(@NonNull String sender, @NonNull String target, @NonNull String body) {
    var withSender = spyFormat.replace("{sender}", sender);
    var withTarget = withSender.replace("{target}", target);

    return withTarget.replace("{message}", body);
  }

  public String formatEnabledOther(@NonNull String player) {
    return enabledOther.replace("{player}", player);
  }

  public String formatDisabledOther(@NonNull String player) {
    return disabledOther.replace("{player}", player);
  }
}
