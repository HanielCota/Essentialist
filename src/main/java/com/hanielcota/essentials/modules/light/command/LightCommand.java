package com.hanielcota.essentials.modules.light.command;

import com.hanielcota.essentials.modules.light.service.LightService;
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

@Command(value = "luz", aliases = "light")
@Permission("essentials.light")
@PermissionForOther(".others")
@Cooldown(duration = "5s")
@Description("Ativa ou desativa a visão noturna do jogador.")
@Syntax("/luz [jogador] | /luz on [jogador] | /luz off [jogador]")
public record LightCommand(LightService service, LightNotifier notifier) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var enabled = this.service.toggle(subject);
    this.notifier.announce(sender, subject, enabled);
  }

  @Subcommand("on")
  public void on(@NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    this.service.set(subject, true);
    this.notifier.announce(sender, subject, true);
  }

  @Subcommand("off")
  public void off(@NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    this.service.set(subject, false);
    this.notifier.announce(sender, subject, false);
  }
}
