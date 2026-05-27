package com.hanielcota.essentials.modules.heal.command;

import com.hanielcota.essentials.command.DualReply;
import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.heal.config.HealConfig;
import com.hanielcota.essentials.modules.heal.service.HealService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.PlayerProvider;
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
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command(value = "curar", aliases = "heal")
@Permission("essentials.heal")
@Cooldown(duration = "5s")
@Description("Restaura a vida do jogador.")
@Syntax("/curar [jogador] | /curar todos")
public record HealCommand(
    ConfigHandle<HealConfig> config,
    HealService service,
    PlayerProvider players,
    ActorFactory actors) {

  @DefaultSubcommand
  @PermissionForOther(".others")
  public CommandResult execute(
      @NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var snap = this.config.value();
    var name = subject.getName();
    var self = Senders.isSelf(sender, subject);

    if (subject.getHealth() <= 0) {
      var dead = snap.whenDead();
      var deadMsg = dead.forSender(self, name);
      return CommandResult.invalidUsage(deadMsg);
    }

    if (!this.service.heal(subject)) {
      var alreadyFull = snap.whenAlreadyFull();
      var alreadyFullMsg = alreadyFull.forSender(self, name);
      return CommandResult.invalidUsage(alreadyFullMsg);
    }

    var messages = snap.whenHealed();
    DualReply.send(sender, subject, this.actors, messages);
    return CommandResult.success();
  }

  @Subcommand("todos")
  @Permission("essentials.heal.all")
  @Description("Restaura a vida de todos os jogadores online.")
  @Syntax("/curar todos")
  public CommandResult healAll(@NonNull CommandActor sender) {
    var online = this.players.all();
    var healed = this.service.healAll(online);

    var snap = this.config.value();
    var summaryMsg = snap.formatHealedAll(healed);

    sender.sendSuccess(summaryMsg);
    return CommandResult.success();
  }
}
