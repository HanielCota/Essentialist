package com.hanielcota.essentials.modules.heal.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.heal.config.HealConfig;
import com.hanielcota.essentials.modules.heal.service.HealService;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.annotation.Alias;
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

@Command(value = "curar", aliases = "heal")
@EssentialsCommand
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
  @PermissionForOther("essentials.heal.others")
  public void execute(CommandActor sender, @TargetOrSelf Player subject) {
    var snap = config.value();
    String name = subject.getName();
    boolean self = sender.uniqueId().equals(subject.getUniqueId().toString());

    if (subject.getHealth() <= 0) {
      sender.sendError(snap.whenDead().forSender(self, name));
      return;
    }

    if (!service.heal(subject)) {
      sender.sendError(snap.whenAlreadyFull().forSender(self, name));
      return;
    }

    var pair = snap.whenHealed();
    var target = framework.actorOf(subject);
    sender.sendDualMessage(target, pair.forSender(self, name), pair.forTarget(name));
  }

  @Subcommand("todos")
  @Alias("all")
  @Permission("essentials.heal.all")
  @Description("Restaura a vida de todos os jogadores online.")
  @Syntax("/curar todos")
  public void healAll(CommandActor sender) {
    int healed = 0;
    for (Player player : players.all()) {

      if (player.getHealth() > 0 && service.heal(player)) {
        healed++;
      }
    }
    sender.sendSuccess(config.value().formatHealedAll(healed));
  }
}
