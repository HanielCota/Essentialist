package com.hanielcota.essentials.modules.repair.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.repair.config.RepairConfig;
import com.hanielcota.essentials.modules.repair.service.RepairService;
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
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import org.bukkit.entity.Player;

@Command(value = "reparar", aliases = "repair")
@Permission("essentials.repair")
@PermissionForOther(".others")
@Cooldown(duration = "5s")
@Description("Repara o item na mão ou o inventário inteiro.")
@Syntax("/reparar [jogador] | /reparar tudo [jogador]")
public record RepairCommand(
    ConfigHandle<RepairConfig> config, RepairService service, PaperCommandFramework framework) {

  @DefaultSubcommand
  public void execute(CommandActor sender, @TargetOrSelf Player subject) {
    var snap = config.value();
    String name = subject.getName();
    boolean self = Senders.isSelf(sender, subject);

    var result = service.repairHand(subject);
    switch (result) {
      case EMPTY_HAND -> sender.sendError(snap.whenEmptyHand().forSender(self, name));
      case NOTHING_TO_REPAIR -> sender.sendError(snap.whenNothingHand().forSender(self, name));
      case REPAIRED -> {
        var pair = snap.whenHandRepaired();
        var target = framework.actorOf(subject);
        String selfMessage = pair.forSender(self, name);
        sender.sendDualMessage(target, selfMessage, pair.forTarget(name));
      }
      default -> throw new IllegalStateException("Unexpected repair result: " + result);
    }
  }

  @Subcommand({"tudo", "all"})
  public void all(CommandActor sender, @TargetOrSelf Player subject) {
    var snap = config.value();
    String name = subject.getName();
    boolean self = Senders.isSelf(sender, subject);

    int repaired = service.repairAll(subject);
    if (repaired == 0) {
      sender.sendError(snap.whenNothingAll().forSender(self, name));
      return;
    }

    var pair = snap.whenAllRepaired();
    String count = Integer.toString(repaired);
    var target = framework.actorOf(subject);
    String selfMessage = pair.forSender(self, name).replace("{count}", count);
    String targetMessage = pair.forTarget(name).replace("{count}", count);

    sender.sendDualMessage(target, selfMessage, targetMessage);
  }
}
