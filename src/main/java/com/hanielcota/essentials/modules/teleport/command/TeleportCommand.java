package com.hanielcota.essentials.modules.teleport.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.teleport.config.TeleportConfig;
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
    ConfigHandle<TeleportConfig> config, PaperCommandFramework framework) {

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

    var targetLocation = target.getLocation();
    var targetName = target.getName();
    var senderName = sender.getName();
    sender
        .teleportAsync(targetLocation)
        .thenAccept(
            success -> {
              if (!Boolean.TRUE.equals(success)) {
                senderActor.sendError(snap.teleportFailed());
                return;
              }
              var targetActor = this.framework.actorOf(target);
              senderActor.sendDualMessage(
                  targetActor,
                  snap.formatToPlayer(targetName),
                  snap.formatTeleportedTo(senderName));
            });
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

    var toLocation = to.getLocation();
    var fromName = from.getName();
    var toName = to.getName();
    var senderName = sender.name();
    var selfMove = Senders.isSelf(sender, from);
    from.teleportAsync(toLocation)
        .thenAccept(
            success -> {
              if (!Boolean.TRUE.equals(success)) {
                sender.sendError(snap.teleportFailed());
                return;
              }
              sender.sendSuccess(snap.formatMoveSender(fromName, toName));
              if (!selfMove) {
                var fromActor = this.framework.actorOf(from);
                fromActor.sendSuccess(snap.formatMoveNotify(senderName));
              }
            });
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
    var currentLocation = sender.getLocation();
    var currentYaw = currentLocation.getYaw();
    var currentPitch = currentLocation.getPitch();
    var destination = new Location(world, x, y, z, currentYaw, currentPitch);

    var minHeight = world.getMinHeight();
    var maxHeight = world.getMaxHeight();
    var worldBorder = world.getWorldBorder();
    if (y < minHeight || y >= maxHeight || !worldBorder.isInside(destination)) {
      senderActor.sendError(snap.invalidPosition());
      return;
    }

    sender
        .teleportAsync(destination)
        .thenAccept(
            success -> {
              if (!Boolean.TRUE.equals(success)) {
                senderActor.sendError(snap.teleportFailed());
                return;
              }
              senderActor.sendSuccess(snap.formatToPos(x, y, z));
            });
  }
}
