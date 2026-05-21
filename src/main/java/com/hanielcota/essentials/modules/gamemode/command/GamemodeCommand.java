package com.hanielcota.essentials.modules.gamemode.command;

import com.hanielcota.essentials.command.ActorMessages;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.gamemode.config.GamemodeConfig;
import com.hanielcota.essentials.modules.gamemode.service.GamemodeService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PermissionForOther;
import io.github.hanielcota.commandframework.annotation.PermissionTemplate;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@Command(value = "gamemode", aliases = "gm")
@EssentialsCommand
@Permission("essentials.gamemode")
@PermissionForOther("essentials.gamemode.others")
@PermissionTemplate("essentials.gamemode.{mode}")
@Cooldown(duration = "3s")
@Description("Altera o modo de jogo do jogador.")
@Syntax("/gamemode <modo> [jogador]")
public record GamemodeCommand(
    ConfigHandle<GamemodeConfig> config, GamemodeService service, ActorMessages messages) {

  @DefaultSubcommand
  public void execute(
      CommandActor sender, @Arg("modo") GameMode mode, @TargetOrSelf Player subject) {
    var snap = config.value();

    var result = service.apply(subject, mode);

    if (result == GamemodeService.Result.ALREADY_IN_MODE) {
      sender.sendError(snap.whenAlreadyInMode(mode).format(true, subject.getName()));
      return;
    }

    messages.notifyTarget(sender, subject, snap.whenUpdated(mode), subject.getName());
  }
}
