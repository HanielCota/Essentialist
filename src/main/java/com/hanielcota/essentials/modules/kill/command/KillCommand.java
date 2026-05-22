package com.hanielcota.essentials.modules.kill.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.kill.config.KillConfig;
import com.hanielcota.essentials.modules.kill.service.KillService;
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

@Command(value = "matar", aliases = "kill")
@Permission("essentials.kill")
@PermissionForOther(".others")
@Cooldown(duration = "5s")
@Confirm(duration = "3s")
@Description("Mata o jogador.")
@Syntax("/matar [jogador]")
public record KillCommand(
    ConfigHandle<KillConfig> config, KillService service, PaperCommandFramework framework) {

  @DefaultSubcommand
  public void execute(CommandActor sender, @TargetOrSelf Player subject) {
    var snap = config.value();
    String name = subject.getName();
    boolean self = sender.uniqueId().equals(subject.getUniqueId().toString());

    if (!service.kill(subject)) {
      sender.sendError(snap.whenAlreadyDead().forSender(self, name));
      return;
    }

    var pair = snap.whenKilled();
    var target = framework.actorOf(subject);
    String selfMessage = pair.forSender(self, name);

    sender.sendDualMessage(target, selfMessage, pair.forTarget(name));
  }
}
