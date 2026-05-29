package com.hanielcota.essentials.modules.god.command;

import com.hanielcota.essentials.modules.god.service.GodService;
import io.github.hanielcota.commandframework.annotation.Command;
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

@Command("god")
@Permission("essentials.god")
@PermissionForOther(".others")
@Description("Ativa ou desativa a invulnerabilidade do jogador.")
@Syntax("/god [jogador] | /god on [jogador] | /god off [jogador]")
public record GodCommand(GodService service, GodNotifier notifier) {

  @DefaultSubcommand
  public CommandResult execute(
      @NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var id = subject.getUniqueId();
    var enabled = this.service.toggle(id);

    this.notifier.announce(sender, subject, enabled);
    return CommandResult.success();
  }

  @Subcommand("on")
  public CommandResult on(@NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var id = subject.getUniqueId();
    this.service.enable(id);

    this.notifier.announce(sender, subject, true);
    return CommandResult.success();
  }

  @Subcommand("off")
  public CommandResult off(@NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var id = subject.getUniqueId();
    this.service.disable(id);

    this.notifier.announce(sender, subject, false);
    return CommandResult.success();
  }
}
