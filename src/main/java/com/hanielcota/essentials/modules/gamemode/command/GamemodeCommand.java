package com.hanielcota.essentials.modules.gamemode.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.gamemode.config.GamemodeConfig;
import com.hanielcota.essentials.modules.gamemode.service.GamemodeService;
import com.hanielcota.essentials.paper.ActorFactory;
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
import lombok.NonNull;
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
    ConfigHandle<GamemodeConfig> config, GamemodeService service, ActorFactory actors) {

  @DefaultSubcommand
  public void execute(
      @NonNull CommandActor sender, @Arg("modo") GameMode mode, @TargetOrSelf Player subject) {
    var snap = this.config.value();
    var name = subject.getName();
    var self = Senders.isSelf(sender, subject);

    var result = this.service.apply(subject, mode);
    if (result == GamemodeService.Result.ALREADY_IN_MODE) {
      var alreadyMessages = snap.whenAlreadyInMode(mode);
      var alreadyMsg = alreadyMessages.forSender(self, name);
      sender.sendError(alreadyMsg);
      return;
    }

    var messages = snap.whenUpdated(mode);
    var target = this.actors.actorOf(subject);
    var selfMessage = messages.forSender(self, name);
    var targetMessage = messages.forTarget(name);

    sender.sendDualMessage(target, selfMessage, targetMessage);
  }
}
