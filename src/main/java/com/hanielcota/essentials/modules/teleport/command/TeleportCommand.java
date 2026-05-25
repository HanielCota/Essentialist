package com.hanielcota.essentials.modules.teleport.command;

import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.DefaultValue;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;

@Command("tp")
@Permission("essentials.tp")
@Cooldown(duration = "3s")
@Description("Teleports to a player, moves a player to another, or teleports to coordinates.")
@Syntax("/tp <player> | /tp <from> <to> | /tp <x> <y> <z>")
public record TeleportCommand(TeleportDispatcher dispatcher) {

  @DefaultSubcommand
  public void execute(
      @NonNull CommandActor sender,
      @Arg("arg1") @NonNull String arg1,
      @Arg("arg2") @DefaultValue("") @NonNull String arg2,
      @Arg("arg3") @DefaultValue("") @NonNull String arg3) {
    this.dispatcher.dispatch(sender, arg1, arg2, arg3);
  }
}
