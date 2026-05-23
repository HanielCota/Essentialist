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
import lombok.NonNull;
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
  public void toPlayer(@NonNull Player sender, @OnlinePlayer @NonNull Player target) {
    var snap = this.config.value();
    var senderActor = this.framework.actorOf(sender);

    if (sender.getUniqueId().equals(target.getUniqueId())) {
      senderActor.sendError(snap.selfTarget());
      return;
    }

    if (!this.service.teleportTo(sender, target.getLocation())) {
      senderActor.sendError(snap.teleportFailed());
      return;
    }

    var targetActor = this.framework.actorOf(target);
    var selfMessage = snap.formatToPlayer(target.getName());
    var otherMessage = snap.formatTeleportedTo(sender.getName());
    senderActor.sendDualMessage(targetActor, selfMessage, otherMessage);
  }

  @Subcommand("move")
  @Permission("essentials.tp.others")
  @Description("Teleporta um jogador até outro.")
  @Syntax("/tp move <de> <para>")
  public void movePlayer(
      @NonNull CommandActor sender,
      @OnlinePlayer @NonNull Player from,
      @OnlinePlayer @NonNull Player to) {
    var snap = this.config.value();

    if (from.getUniqueId().equals(to.getUniqueId())) {
      sender.sendError(snap.selfTarget());
      return;
    }

    if (!this.service.teleportTo(from, to.getLocation())) {
      sender.sendError(snap.teleportFailed());
      return;
    }

    var moveSenderMsg = snap.formatMoveSender(from.getName(), to.getName());
    sender.sendSuccess(moveSenderMsg);

    if (!Senders.isSelf(sender, from)) {
      var moveNotifyMsg = snap.formatMoveNotify(sender.name());
      this.framework.actorOf(from).sendSuccess(moveNotifyMsg);
    }
  }

  @Subcommand("pos")
  @PlayerOnly
  @Description("Teleporta para coordenadas específicas.")
  @Syntax("/tp pos <x> <y> <z>")
  public void toPos(
      @NonNull Player sender, @Arg("x") double x, @Arg("y") double y, @Arg("z") double z) {
    var snap = this.config.value();
    var senderActor = this.framework.actorOf(sender);
    var world = sender.getWorld();
    var current = sender.getLocation();
    var destination = new Location(world, x, y, z, current.getYaw(), current.getPitch());

    if (y < world.getMinHeight()
        || y >= world.getMaxHeight()
        || !world.getWorldBorder().isInside(destination)) {
      senderActor.sendError(snap.invalidPosition());
      return;
    }

    if (!this.service.teleportTo(sender, destination)) {
      senderActor.sendError(snap.teleportFailed());
      return;
    }

    var toPosMsg = snap.formatToPos(x, y, z);
    senderActor.sendSuccess(toPosMsg);
  }
}
