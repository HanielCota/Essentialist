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
import lombok.NonNull;
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
  public void execute(@NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var snap = this.config.value();
    var clearArmor = snap.clearArmor();
    var removed = this.service.clear(subject, clearArmor);
    var name = subject.getName();
    var self = Senders.isSelf(sender, subject);

    if (removed == 0) {
      var emptyMessages = snap.whenEmpty();
      var emptyMsg = emptyMessages.forSender(self, name);
      sender.sendError(emptyMsg);
      return;
    }

    var messages = snap.whenCleared();
    var count = Integer.toString(removed);
    var target = this.framework.actorOf(subject);

    var selfBase = messages.forSender(self, name);
    var selfMessage = selfBase.replace("{count}", count);

    var targetBase = messages.forTarget(name);
    var targetMessage = targetBase.replace("{count}", count);

    sender.sendDualMessage(target, selfMessage, targetMessage);
  }
}
