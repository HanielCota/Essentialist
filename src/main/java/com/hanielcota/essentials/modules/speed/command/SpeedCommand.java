package com.hanielcota.essentials.modules.speed.command;

import com.hanielcota.essentials.command.Senders;
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
import io.github.hanielcota.commandframework.annotation.Range;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("speed")
@Permission("essentials.speed")
@Cooldown(duration = "3s")
@Description("Ajusta a velocidade de andar ou voar do jogador.")
@Syntax("/speed walk <valor> [jogador] | /speed fly <valor> [jogador] | /speed reset [jogador]")
public record SpeedCommand(
    ConfigHandle<SpeedConfig> config, SpeedService service, PaperCommandFramework framework) {

  @DefaultSubcommand
  public void showUsage(@NonNull CommandActor sender) {
    var snap = this.config.value();
    sender.sendMessage(snap.usage());
  }

  @Subcommand("walk")
  @PermissionForOther(".others")
  public void walk(
      @NonNull CommandActor sender,
      @Range(min = 1, max = 10) @Arg("valor") int valor,
      @TargetOrSelf Player subject) {
    var snap = this.config.value();
    if (!this.service.setWalkSpeed(subject, valor)) {
      sender.sendError(snap.invalid());
      return;
    }

    announce(sender, subject, snap.whenWalkSet(valor));
  }

  @Subcommand("fly")
  @PermissionForOther(".others")
  public void fly(
      @NonNull CommandActor sender,
      @Range(min = 1, max = 10) @Arg("valor") int valor,
      @TargetOrSelf Player subject) {
    var snap = this.config.value();
    if (!this.service.setFlySpeed(subject, valor)) {
      sender.sendError(snap.invalid());
      return;
    }

    announce(sender, subject, snap.whenFlySet(valor));
  }

  @Subcommand({"reset", "resetar"})
  @PermissionForOther(".others")
  public void reset(@NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var snap = this.config.value();
    this.service.reset(subject);
    announce(sender, subject, snap.whenReset());
  }

  private void announce(
      @NonNull CommandActor sender, @NonNull Player subject, @NonNull MessagePair messages) {
    var name = subject.getName();
    var isSelf = Senders.isSelf(sender, subject);
    var target = this.framework.actorOf(subject);

    sender.sendDualMessage(target, messages.forSender(isSelf, name), messages.forTarget(name));
  }
}
