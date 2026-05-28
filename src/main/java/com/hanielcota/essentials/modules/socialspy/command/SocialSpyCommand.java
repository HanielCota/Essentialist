package com.hanielcota.essentials.modules.socialspy.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.modules.socialspy.service.SocialSpyService;
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

@Command("socialspy")
@Permission("essentials.socialspy")
@PermissionForOther(".others")
@Description("Ativa ou desativa a observação de mensagens privadas.")
@Syntax("/socialspy [jogador]")
public record SocialSpyCommand(SocialSpyService service, SocialSpyNotifier notifier) {

  @DefaultSubcommand
  public CommandResult execute(
      @NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var subjectId = subject.getUniqueId();
    var subjectName = subject.getName();
    var self = Senders.isSelf(sender, subject);

    var enabled = this.service.enter(subjectId);
    if (!enabled) {
      this.service.exit(subjectId);
    }

    this.notifier.sendToggle(sender, enabled, self, subjectName);

    return CommandResult.success();
  }
}
