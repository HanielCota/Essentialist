package com.hanielcota.essentials.modules.kill.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.kill.config.KillConfig;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Confirm;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PermissionForOther;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command(value = "matar", aliases = "kill")
@Permission("essentials.kill")
@PermissionForOther(".others")
@Cooldown(duration = "5s")
@Confirm(duration = "3s")
@Description("Mata o jogador.")
@Syntax("/matar [jogador]")
public record KillCommand(ConfigHandle<KillConfig> config, PaperCommandFramework framework) {

  private static final String EXEMPT_PERMISSION = "essentials.kill.exempt";

  @DefaultSubcommand
  public void execute(@NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var snap = this.config.value();
    var name = subject.getName();
    var self = Senders.isSelf(sender, subject);

    // Exempt only applies when killing someone else — a staff member with both essentials.kill and
    // essentials.kill.exempt can still kill themselves.
    if (!self && subject.hasPermission(EXEMPT_PERMISSION)) {
      var exemptMsg = snap.formatExempt(name);
      sender.sendError(exemptMsg);
      return;
    }

    if (subject.getHealth() <= 0) {
      var alreadyDead = snap.whenAlreadyDead();
      var alreadyDeadMsg = alreadyDead.forSender(self, name);
      sender.sendError(alreadyDeadMsg);
      return;
    }

    subject.setHealth(0);

    var messages = snap.whenKilled();
    var target = this.framework.actorOf(subject);
    var selfMsg = messages.forSender(self, name);
    var targetMsg = messages.forTarget(name);

    sender.sendDualMessage(target, selfMsg, targetMsg);
  }
}
