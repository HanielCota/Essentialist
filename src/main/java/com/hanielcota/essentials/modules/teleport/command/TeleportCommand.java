package com.hanielcota.essentials.modules.teleport.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.modules.teleport.service.TeleportService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.OnlinePlayer;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("tp")
@Permission("essentials.tp")
@Cooldown(duration = "3s")
@Description("Teleporta o jogador para outro jogador ou coordenadas.")
@Syntax("/tp <jogador> | /tp move <de> <para> | /tp pos <x> <y> <z>")
public record TeleportCommand(PaperCommandFramework framework, TeleportNotifier notifier) {

  @DefaultSubcommand
  @PlayerOnly
  @Description("Teleporta você até outro jogador.")
  @Syntax("/tp <jogador>")
  public void toPlayer(@NonNull Player sender, @OnlinePlayer @NonNull Player target) {
    var senderActor = this.framework.actorOf(sender);
    var senderName = sender.getName();
    var targetName = target.getName();

    TeleportService.toPlayer(sender, target)
        .thenAccept(
            outcome ->
                this.notifier.notifyToPlayer(senderActor, target, senderName, targetName, outcome));
  }

  @Subcommand("move")
  @Permission("essentials.tp.others")
  @Description("Teleporta um jogador até outro.")
  @Syntax("/tp move <de> <para>")
  public void movePlayer(
      @NonNull CommandActor sender,
      @OnlinePlayer @NonNull Player from,
      @OnlinePlayer @NonNull Player to) {
    var fromName = from.getName();
    var toName = to.getName();
    var senderName = sender.name();
    var selfMove = Senders.isSelf(sender, from);

    TeleportService.movePlayer(from, to)
        .thenAccept(
            outcome ->
                this.notifier.notifyMove(
                    sender, from, fromName, toName, senderName, selfMove, outcome));
  }

  @Subcommand("pos")
  @PlayerOnly
  @Description("Teleporta para coordenadas específicas.")
  @Syntax("/tp pos <x> <y> <z>")
  public void toPos(
      @NonNull Player sender, @Arg("x") double x, @Arg("y") double y, @Arg("z") double z) {
    var senderActor = this.framework.actorOf(sender);

    TeleportService.toPosition(sender, x, y, z)
        .thenAccept(outcome -> this.notifier.notifyToPos(senderActor, x, y, z, outcome));
  }
}
