package com.hanielcota.essentials.modules.repair.command;

import com.hanielcota.essentials.command.DualReply;
import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.repair.config.RepairConfig;
import com.hanielcota.essentials.modules.repair.service.RepairService;
import com.hanielcota.essentials.paper.ActorFactory;
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
import io.github.hanielcota.commandframework.core.CommandResult;
import java.util.function.UnaryOperator;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command(value = "reparar", aliases = "repair")
@Permission("essentials.repair")
@PermissionForOther(".others")
@Cooldown(duration = "5s")
@Description("Repara o item na mão ou o inventário inteiro.")
@Syntax("/reparar [jogador] | /reparar tudo [jogador]")
public record RepairCommand(
    ConfigHandle<RepairConfig> config, RepairService service, ActorFactory actors) {

  @DefaultSubcommand
  public CommandResult execute(
      @NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var snap = this.config.value();
    var name = subject.getName();
    var self = Senders.isSelf(sender, subject);

    var result = this.service.repairHand(subject);
    return switch (result) {
      case EMPTY_HAND -> {
        var emptyMessages = snap.whenEmptyHand();
        var emptyMsg = emptyMessages.forSender(self, name);
        yield CommandResult.invalidUsage(emptyMsg);
      }
      case NOTHING_TO_REPAIR -> {
        var nothingMessages = snap.whenNothingHand();
        var nothingMsg = nothingMessages.forSender(self, name);
        yield CommandResult.invalidUsage(nothingMsg);
      }
      case REPAIRED -> {
        var messages = snap.whenHandRepaired();
        DualReply.send(sender, subject, this.actors, messages);
        yield CommandResult.success();
      }
    };
  }

  @Subcommand({"tudo", "all"})
  public CommandResult all(@NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var snap = this.config.value();
    var name = subject.getName();
    var self = Senders.isSelf(sender, subject);

    var repaired = this.service.repairAll(subject);
    if (repaired == 0) {
      var nothingMessages = snap.whenNothingAll();
      var nothingMsg = nothingMessages.forSender(self, name);
      return CommandResult.invalidUsage(nothingMsg);
    }

    var messages = snap.whenAllRepaired();
    var count = Integer.toString(repaired);
    var replacer = (UnaryOperator<String>) line -> line.replace("{count}", count);
    DualReply.send(sender, subject, this.actors, messages, replacer);
    return CommandResult.success();
  }
}
