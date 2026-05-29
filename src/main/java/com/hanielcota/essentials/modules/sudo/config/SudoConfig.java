package com.hanielcota.essentials.modules.sudo.config;

import com.hanielcota.essentials.shared.Placeholders;
import lombok.NonNull;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;

@ConfigSerializable
public record SudoConfig(
    @Comment("Confirmation shown to the sender. Placeholders: {player}, {command}.")
        String executed,
    @Comment("Shown when no command is given.") String emptyCommand) {

  public static SudoConfig defaults() {
    return new SudoConfig(
        "<green>Ran <gold>/{command}</gold> as <gold>{player}</gold>.",
        "<red>You must provide a command to run.");
  }

  public String formatExecuted(@NonNull String player, @NonNull String command) {
    return Placeholders.format(executed, "player", player, "command", command);
  }
}
