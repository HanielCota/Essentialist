package com.hanielcota.essentials.modules.speed.command;

import com.hanielcota.essentials.config.ConfigHandle;
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
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("speed")
@Permission("essentials.speed")
@Cooldown(duration = "3s")
@Description("Ajusta a velocidade de andar ou voar do jogador.")
@Syntax("/speed walk <valor> [jogador] | /speed fly <valor> [jogador] | /speed reset [jogador]")
public record SpeedCommand(
    ConfigHandle<SpeedConfig> config, SpeedService service, SpeedNotifier notifier) {

  @DefaultSubcommand
  public CommandResult showUsage(@NonNull CommandActor sender) {
    var snap = this.config.value();
    var usageMsg = snap.formatUsage();
    sender.sendMessage(usageMsg);
    return CommandResult.success();
  }

  @Subcommand("walk")
  @PermissionForOther(".others")
  public CommandResult walk(
      @NonNull CommandActor sender, @Arg("valor") int valor, @TargetOrSelf Player subject) {
    var snap = this.config.value();

    if (!this.service.setWalkSpeed(subject, valor)) {
      var invalidMsg = snap.formatInvalid();
      return CommandResult.invalidUsage(invalidMsg);
    }

    var messages = snap.whenWalkSet(valor);
    this.notifier.announce(sender, subject, messages);
    return CommandResult.success();
  }

  @Subcommand("fly")
  @PermissionForOther(".others")
  public CommandResult fly(
      @NonNull CommandActor sender, @Arg("valor") int valor, @TargetOrSelf Player subject) {
    var snap = this.config.value();

    if (!this.service.setFlySpeed(subject, valor)) {
      var invalidMsg = snap.formatInvalid();
      return CommandResult.invalidUsage(invalidMsg);
    }

    var messages = snap.whenFlySet(valor);
    this.notifier.announce(sender, subject, messages);
    return CommandResult.success();
  }

  @Subcommand({"reset", "resetar"})
  @PermissionForOther(".others")
  public CommandResult reset(@NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var snap = this.config.value();
    this.service.reset(subject);

    var messages = snap.whenReset();
    this.notifier.announce(sender, subject, messages);
    return CommandResult.success();
  }
}
