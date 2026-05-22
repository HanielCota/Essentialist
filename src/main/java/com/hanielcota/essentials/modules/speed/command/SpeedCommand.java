package com.hanielcota.essentials.modules.speed.command;

import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PermissionForOther;
import io.github.hanielcota.commandframework.annotation.Range;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import org.bukkit.entity.Player;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.config.MessagePair;
import com.hanielcota.essentials.modules.speed.config.SpeedConfig;
import com.hanielcota.essentials.modules.speed.service.SpeedService;

@Command("speed")
@Permission("essentials.speed")
@Cooldown(duration = "3s")
@Description("Ajusta a velocidade de andar ou voar do jogador.")
@Syntax("/speed walk <valor> [jogador] | /speed fly <valor> [jogador] | /speed reset [jogador]")
public record SpeedCommand(
    ConfigHandle<SpeedConfig> config, SpeedService service, PaperCommandFramework framework) {

  @DefaultSubcommand
  public void showUsage(CommandActor sender) {
    sender.sendMessage(config.value().usage());
  }

  @Subcommand("walk")
  @PermissionForOther(".others")
  public void walk(CommandActor sender, @Range(min = 1, max = 10) @Arg("valor") int valor, @TargetOrSelf Player subject) {
    if (!service.setWalkSpeed(subject, valor)) {
      sender.sendError(config.value().invalid());
      return;
    }

    announce(sender, subject, config.value().whenWalkSet(valor));
  }

  @Subcommand("fly")
  @PermissionForOther(".others")
  public void fly(CommandActor sender, @Range(min = 1, max = 10) @Arg("valor") int valor, @TargetOrSelf Player subject) {
    if (!service.setFlySpeed(subject, valor)) {
      sender.sendError(config.value().invalid());
      return;
    }

    announce(sender, subject, config.value().whenFlySet(valor));
  }

  @Subcommand({"reset", "resetar"})
  @PermissionForOther(".others")
  public void reset(CommandActor sender, @TargetOrSelf Player subject) {
    service.reset(subject);
    announce(sender, subject, config.value().whenReset());
  }

  private void announce(CommandActor sender, Player subject, MessagePair pair) {
    var name = subject.getName();
    var isSelf = sender.uniqueId().equals(subject.getUniqueId().toString());
    var target = framework.actorOf(subject);

    sender.sendDualMessage(target, pair.forSender(isSelf, name), pair.forTarget(name));
  }
}
