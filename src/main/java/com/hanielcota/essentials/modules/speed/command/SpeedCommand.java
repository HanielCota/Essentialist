package com.hanielcota.essentials.modules.speed.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.config.MessagePair;
import com.hanielcota.essentials.modules.speed.config.SpeedConfig;
import com.hanielcota.essentials.modules.speed.service.SpeedService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PermissionForOther;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import org.bukkit.entity.Player;

@Command("speed")
@EssentialsCommand
@Permission("essentials.speed")
@Cooldown(duration = "3s")
@Description("Ajusta a velocidade de andar ou voar do jogador.")
@Syntax("/speed walk <valor> [jogador] | /speed fly <valor> [jogador]")
public record SpeedCommand(
    ConfigHandle<SpeedConfig> config, SpeedService service, PaperCommandFramework framework) {

  @DefaultSubcommand
  public void showUsage(CommandActor sender) {
    sender.sendMessage(config.value().usage());
  }

  @Subcommand("walk")
  @PermissionForOther("essentials.speed.others")
  public void walk(CommandActor sender, @Arg("valor") int valor, @TargetOrSelf Player subject) {
    if (!service.setWalkSpeed(subject, valor)) {
      sender.sendError(config.value().invalid());
      return;
    }

    announce(sender, subject, valor, config.value().whenWalkSet());
  }

  @Subcommand("fly")
  @PermissionForOther("essentials.speed.others")
  public void fly(CommandActor sender, @Arg("valor") int valor, @TargetOrSelf Player subject) {
    if (!service.setFlySpeed(subject, valor)) {
      sender.sendError(config.value().invalid());
      return;
    }

    announce(sender, subject, valor, config.value().whenFlySet());
  }

  private void announce(CommandActor sender, Player subject, int valor, MessagePair pair) {
    var name = subject.getName();
    var isSelf = sender.uniqueId().equals(subject.getUniqueId().toString());
    var target = framework.actorOf(subject);

    var valueStr = Integer.toString(valor);
    var selfMessage = pair.forSender(isSelf, name).replace("{valor}", valueStr);
    var targetMessage = pair.forTarget(name).replace("{valor}", valueStr);

    sender.sendDualMessage(target, selfMessage, targetMessage);
  }
}
