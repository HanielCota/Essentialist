package com.hanielcota.essentials.modules.fly.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.fly.config.FlyConfig;
import com.hanielcota.essentials.modules.fly.service.FlyService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PermissionForOther;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import org.bukkit.entity.Player;

@Command("fly")
@EssentialsCommand
@Permission("essentials.fly")
@PermissionForOther("essentials.fly.others")
@Cooldown(duration = "5s")
@Description("Ativa ou desativa o modo voo do jogador.")
@Syntax("/fly [jogador]")
public record FlyCommand(
    ConfigHandle<FlyConfig> config, FlyService service, PaperCommandFramework framework) {

  @DefaultSubcommand
  public void execute(CommandActor sender, @TargetOrSelf Player subject) {
    var result = service.toggle(subject);
    var snap = config.value();
    String name = subject.getName();
    boolean self = sender.uniqueId().equals(subject.getUniqueId().toString());

    if (result == FlyService.Result.UNSUPPORTED) {
      sender.sendError(snap.unsupportedGamemode().forSender(self, name));
      return;
    }

    var pair = snap.toggle(result == FlyService.Result.ENABLED);
    var target = framework.actorOf(subject);
    String selfMessage = pair.forSender(self, name);

    sender.sendDualMessage(target, selfMessage, pair.forTarget(name));
  }
}
