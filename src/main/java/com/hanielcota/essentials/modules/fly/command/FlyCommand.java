package com.hanielcota.essentials.modules.fly.command;

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
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("fly")
@Permission("essentials.fly")
@PermissionForOther(".others")
@Cooldown(duration = "5s")
@Description("Ativa ou desativa o modo voo do jogador.")
@Syntax("/fly [jogador] | /fly on [jogador] | /fly off [jogador]")
public record FlyCommand(FlyService service, FlyNotifier notifier) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var result = this.service.toggle(subject);
    this.notifier.announce(sender, subject, result);
  }

  @Subcommand("on")
  public void on(@NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var result = this.service.set(subject, true);
    this.notifier.announce(sender, subject, result);
  }

  @Subcommand("off")
  public void off(@NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var result = this.service.set(subject, false);
    this.notifier.announce(sender, subject, result);
  }
}
