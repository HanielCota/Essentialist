package com.hanielcota.essentials.modules.kill.command;

import com.hanielcota.essentials.command.DualReply;
import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.kill.config.KillConfig;
import com.hanielcota.essentials.paper.ActorFactory;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Confirm;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PermissionForOther;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import io.github.hanielcota.commandframework.core.CommandStatus;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command(value = "matar", aliases = "kill")
@Permission("essentials.kill")
@PermissionForOther(".others")
@Confirm(duration = "3s")
@Description("Mata o jogador.")
@Syntax("/matar [jogador]")
public record KillCommand(ConfigHandle<KillConfig> config, ActorFactory actors) {

  @DefaultSubcommand
  public CommandResult execute(
      @NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var snap = this.config.value();
    var name = subject.getName();
    var self = Senders.isSelf(sender, subject);
    var exemptPermission = snap.exemptPermission();

    // Exempt only applies when killing someone else — a staff member with both essentials.kill and
    // the exempt node can still kill themselves.
    if (!self && subject.hasPermission(exemptPermission)) {
      var exemptMsg = snap.formatExempt(name);
      return CommandResult.failure(CommandStatus.NO_PERMISSION, exemptMsg);
    }

    if (subject.getHealth() <= 0) {
      var alreadyDead = snap.whenAlreadyDead();
      var alreadyDeadMsg = alreadyDead.forSender(self, name);
      return CommandResult.invalidUsage(alreadyDeadMsg);
    }

    subject.setHealth(0);

    var messages = snap.whenKilled();
    DualReply.send(sender, subject, this.actors, messages);
    return CommandResult.success();
  }
}
