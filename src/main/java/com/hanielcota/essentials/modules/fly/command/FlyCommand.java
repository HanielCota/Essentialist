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
import lombok.NonNull;
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
  public void execute(@NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var result = this.service.toggle(subject);
    announce(sender, subject, result);
  }

  @Subcommand("on")
  public void on(@NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var result = this.service.set(subject, true);
    announce(sender, subject, result);
  }

  @Subcommand("off")
  public void off(@NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var result = this.service.set(subject, false);
    announce(sender, subject, result);
  }

  private void announce(
      @NonNull CommandActor sender, @NonNull Player subject, @NonNull FlyService.Result result) {
    var snap = this.config.value();
    var name = subject.getName();
    var self = Senders.isSelf(sender, subject);

    if (result == FlyService.Result.UNSUPPORTED) {
      var unsupported = snap.unsupportedGamemode();
      var unsupportedMsg = unsupported.forSender(self, name);
      sender.sendError(unsupportedMsg);
      return;
    }

    var enabled = result == FlyService.Result.ENABLED;
    var messages = snap.toggle(enabled);
    var target = this.framework.actorOf(subject);

    var senderMsg = messages.forSender(self, name);
    var targetMsg = messages.forTarget(name);

    sender.sendDualMessage(target, senderMsg, targetMsg);
  }
}
