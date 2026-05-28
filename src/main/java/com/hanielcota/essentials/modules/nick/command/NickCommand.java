package com.hanielcota.essentials.modules.nick.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.modules.nick.service.NickOperationService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PermissionForOther;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("nick")
@Permission("essentials.nick")
@PermissionForOther(".others")
@Description("Define ou remove o apelido de um jogador.")
@Syntax("/nick <nome|off> [jogador]")
public record NickCommand(NickOperationService operations, NickNotifier notifier) {

  private static final String OFF_KEYWORD = "off";

  @DefaultSubcommand
  public CommandResult execute(
      @NonNull CommandActor sender,
      @Arg("nick") String nick,
      @TargetOrSelf @NonNull Player subject) {
    var trimmed = nick.strip();
    var self = Senders.isSelf(sender, subject);

    if (trimmed.equalsIgnoreCase(OFF_KEYWORD)) {
      var outcome = this.operations.reset(subject);
      this.notifier.sendResetOutcome(sender, subject, self, outcome);

      return CommandResult.success();
    }

    var outcome = this.operations.set(subject, trimmed);
    this.notifier.sendSetOutcome(sender, subject, self, outcome);

    return CommandResult.success();
  }
}
