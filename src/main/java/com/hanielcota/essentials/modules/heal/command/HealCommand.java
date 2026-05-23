package com.hanielcota.essentials.modules.heal.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.heal.config.HealConfig;
import com.hanielcota.essentials.modules.heal.service.HealService;
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
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
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
    PaperCommandFramework framework) {

  @DefaultSubcommand
  @PermissionForOther(".others")
  public void execute(@NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    var snap = this.config.value();
    String name = subject.getName();
    boolean self = Senders.isSelf(sender, subject);

    if (subject.getHealth() <= 0) {
      sender.sendError(snap.whenDead().forSender(self, name));
      return;
    }

    if (!this.service.heal(subject)) {
      sender.sendError(snap.whenAlreadyFull().forSender(self, name));
      return;
    }

    var messages = snap.whenHealed();
    var target = this.framework.actorOf(subject);
    sender.sendDualMessage(target, messages.forSender(self, name), messages.forTarget(name));
  }

  @Subcommand("todos")
  @Permission("essentials.heal.all")
  @Description("Restaura a vida de todos os jogadores online.")
  @Syntax("/curar todos")
  public void healAll(@NonNull CommandActor sender) {
    int healed = 0;
    for (Player player : this.players.all()) {

      if (player.getHealth() > 0 && this.service.heal(player)) {
        healed++;
      }
    }
    sender.sendSuccess(this.config.value().formatHealedAll(healed));
  }
}
