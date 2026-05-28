package com.hanielcota.essentials.modules.back.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;

@Command("back")
@EssentialsCommand
@Permission("essentials.back")
@PlayerOnly
@Cooldown(duration = "5s")
@Description("Retorna à localização anterior ou abre o histórico de teleportes.")
@Syntax("/back")
public record BackCommand(BackOrchestrator orchestrator) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor) {
    return this.orchestrator.openBack(actor);
  }
}
