package com.hanielcota.essentials.modules.back.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PermissionForOther;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("back")
@EssentialsCommand
@Permission("essentials.back")
@PlayerOnly
@Description("Retorna a localizacao anterior ou abre o historico de teleportes.")
@Syntax("/back | /back ! | /back <player> | /back clear [player]")
public record BackCommand(BackOrchestrator orchestrator) {

  @DefaultSubcommand
  @PermissionForOther(".others")
  public CommandResult openOrView(
      @NonNull CommandActor actor, @TargetOrSelf @NonNull Player subject) {
    if (isSelf(actor, subject)) {
      return this.orchestrator.openBack(actor);
    }
    return this.orchestrator.viewBack(actor, subject);
  }

  @Subcommand("!")
  public CommandResult forceTeleport(@NonNull CommandActor actor) {
    return this.orchestrator.forceBack(actor);
  }

  @Subcommand("clear")
  @Permission("essentials.back.clear")
  @PermissionForOther(".others")
  public CommandResult clear(@NonNull CommandActor actor, @TargetOrSelf @NonNull Player subject) {
    return this.orchestrator.clearBack(actor, subject);
  }

  private static boolean isSelf(@NonNull CommandActor actor, @NonNull Player subject) {
    var actorId = actor.uniqueId();
    var subjectId = subject.getUniqueId().toString();

    return actorId.equals(subjectId);
  }
}
