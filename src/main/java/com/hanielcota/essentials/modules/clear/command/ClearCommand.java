package com.hanielcota.essentials.modules.clear.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.clear.config.ClearConfig;
import com.hanielcota.essentials.modules.clear.service.ClearService;
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
import org.bukkit.entity.Player;

@Command(value = "limpar", aliases = "clear")
@Permission("essentials.clear")
@PermissionForOther(".others")
@Cooldown(duration = "5s")
@Confirm(duration = "3s")
@Description("Limpa o inventário do jogador.")
@Syntax("/limpar [jogador]")
public record ClearCommand(
    ConfigHandle<ClearConfig> config, ClearService service, PaperCommandFramework framework) {

  @DefaultSubcommand
  public void execute(CommandActor sender, @TargetOrSelf Player subject) {
    var snap = config.value();
    var removed = service.clear(subject, snap.clearArmor());
    var name = subject.getName();
    var self = Senders.isSelf(sender, subject);

    if (removed == 0) {
      sender.sendError(snap.whenEmpty().forSender(self, name));
      return;
    }

    var messages = snap.whenCleared();
    var count = Integer.toString(removed);
    var target = framework.actorOf(subject);
    var selfMessage = messages.forSender(self, name).replace("{count}", count);
    var targetMessage = messages.forTarget(name).replace("{count}", count);

    sender.sendDualMessage(target, selfMessage, targetMessage);
  }
}
