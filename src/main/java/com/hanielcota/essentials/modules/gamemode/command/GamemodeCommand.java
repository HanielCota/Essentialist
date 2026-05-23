package com.hanielcota.essentials.modules.gamemode.command;

import com.hanielcota.essentials.command.Senders;
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
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

@Command(value = "gamemode", aliases = "gm")
@Permission("essentials.gamemode")
@PermissionForOther(".others")
@PermissionTemplate("essentials.gamemode.{modo}")
@Cooldown(duration = "3s")
@Description("Altera o modo de jogo do jogador.")
@Syntax("/gamemode <modo> [jogador]")
public record GamemodeCommand(
    ConfigHandle<GamemodeConfig> config, GamemodeService service, PaperCommandFramework framework) {

  @DefaultSubcommand
  public void execute(
      CommandActor sender, @Arg("modo") GameMode mode, @TargetOrSelf Player subject) {
    var snap = config.value();
    var name = subject.getName();
    var self = Senders.isSelf(sender, subject);

    if (service.apply(subject, mode) == GamemodeService.Result.ALREADY_IN_MODE) {
      sender.sendError(snap.whenAlreadyInMode(mode).forSender(self, name));
      return;
    }

    var messages = snap.whenUpdated(mode);
    var target = framework.actorOf(subject);
    var selfMessage = messages.forSender(self, name);

    sender.sendDualMessage(target, selfMessage, messages.forTarget(name));
  }
}
