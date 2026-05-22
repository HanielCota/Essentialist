package com.hanielcota.essentials.modules.teleport.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.teleport.config.TeleportConfig;
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
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Command("tp")
@Permission("essentials.tp")
@Cooldown(duration = "3s")
@Description("Teleporta o jogador para outro jogador ou coordenadas.")
@Syntax("/tp <jogador> | /tp move <de> <para> | /tp pos <x> <y> <z>")
public record TeleportCommand(
    ConfigHandle<TeleportConfig> config, TeleportService service, PaperCommandFramework framework) {

  @DefaultSubcommand
  @PlayerOnly
  @Description("Teleporta você até outro jogador.")
  @Syntax("/tp <jogador>")
  public void toPlayer(Player sender, @OnlinePlayer Player target) {
    var snap = config.value();
    var senderActor = framework.actorOf(sender);

    if (sender.getUniqueId().equals(target.getUniqueId())) {
      senderActor.sendError(snap.selfTarget());
      return;
    }

    if (!service.teleportTo(sender, target.getLocation())) {
      senderActor.sendError(snap.teleportFailed());
      return;
    }

    var targetActor = framework.actorOf(target);
    String selfMessage = snap.formatToPlayer(target.getName());
    String otherMessage = snap.formatTeleportedTo(sender.getName());
    senderActor.sendDualMessage(targetActor, selfMessage, otherMessage);
  }

  @Subcommand("move")
  @Permission("essentials.tp.others")
  @Description("Teleporta um jogador até outro.")
  @Syntax("/tp move <de> <para>")
  public void movePlayer(CommandActor sender, @OnlinePlayer Player from, @OnlinePlayer Player to) {
    var snap = config.value();

    if (from.getUniqueId().equals(to.getUniqueId())) {
      sender.sendError(snap.selfTarget());
      return;
    }

    if (!service.teleportTo(from, to.getLocation())) {
      sender.sendError(snap.teleportFailed());
      return;
    }

    sender.sendSuccess(snap.formatMoveSender(from.getName(), to.getName()));

    if (!Senders.isSelf(sender, from)) {
      framework.actorOf(from).sendSuccess(snap.formatMoveNotify(sender.name()));
    }
  }

  @Subcommand("pos")
  @PlayerOnly
  @Description("Teleporta para coordenadas específicas.")
  @Syntax("/tp pos <x> <y> <z>")
  public void toPos(Player sender, @Arg("x") double x, @Arg("y") double y, @Arg("z") double z) {
    var snap = config.value();
    var senderActor = framework.actorOf(sender);
    var world = sender.getWorld();
    var current = sender.getLocation();
    var destination = new Location(world, x, y, z, current.getYaw(), current.getPitch());

    if (y < world.getMinHeight()
        || y >= world.getMaxHeight()
        || !world.getWorldBorder().isInside(destination)) {
      senderActor.sendError(snap.invalidPosition());
      return;
    }

    if (!service.teleportTo(sender, destination)) {
      senderActor.sendError(snap.teleportFailed());
      return;
    }

    senderActor.sendSuccess(snap.formatToPos(x, y, z));
  }
}
