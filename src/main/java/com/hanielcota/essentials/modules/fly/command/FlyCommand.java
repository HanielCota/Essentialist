package com.hanielcota.essentials.modules.fly.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.fly.config.FlyConfig;
import com.hanielcota.essentials.modules.fly.service.FlyService;
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

@Command("fly")
@Permission("essentials.fly")
@PermissionForOther(".others")
@Cooldown(duration = "5s")
@Description("Ativa ou desativa o modo voo do jogador.")
@Syntax("/fly [jogador] | /fly on [jogador] | /fly off [jogador]")
public record FlyCommand(
    ConfigHandle<FlyConfig> config, FlyService service, PaperCommandFramework framework) {

  @DefaultSubcommand
  public void execute(CommandActor sender, @TargetOrSelf Player subject) {
    announce(sender, subject, service.toggle(subject));
  }

  @Subcommand("on")
  public void on(CommandActor sender, @TargetOrSelf Player subject) {
    announce(sender, subject, service.set(subject, true));
  }

  @Subcommand("off")
  public void off(CommandActor sender, @TargetOrSelf Player subject) {
    announce(sender, subject, service.set(subject, false));
  }

  private void announce(CommandActor sender, Player subject, FlyService.Result result) {
    var snap = config.value();
    String name = subject.getName();
    boolean self = Senders.isSelf(sender, subject);

    if (result == FlyService.Result.UNSUPPORTED) {
      sender.sendError(snap.unsupportedGamemode().forSender(self, name));
      return;
    }

    var pair = snap.toggle(result == FlyService.Result.ENABLED);
    var target = framework.actorOf(subject);
    sender.sendDualMessage(target, pair.forSender(self, name), pair.forTarget(name));
  }
}
