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

    var senderId = sender.getUniqueId();
    var targetId = target.getUniqueId();
    if (senderId.equals(targetId)) {
      var selfTargetMsg = snap.selfTarget();
      senderActor.sendError(selfTargetMsg);
      return;
    }

    var targetLocation = target.getLocation();
    var targetName = target.getName();
    var senderName = sender.getName();

    var teleport = sender.teleportAsync(targetLocation);
    teleport.thenAccept(
        success -> onToPlayerComplete(success, snap, senderActor, target, senderName, targetName));
  }

  private void onToPlayerComplete(
      Boolean success,
      TeleportConfig snap,
      CommandActor senderActor,
      Player target,
      String senderName,
      String targetName) {
    if (!Boolean.TRUE.equals(success)) {
      var failedMsg = snap.teleportFailed();
      senderActor.sendError(failedMsg);
      return;
    }

    var targetActor = this.framework.actorOf(target);
    var senderMsg = snap.formatToPlayer(targetName);
    var targetMsg = snap.formatTeleportedTo(senderName);

    senderActor.sendDualMessage(targetActor, senderMsg, targetMsg);
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

    var fromId = from.getUniqueId();
    var toId = to.getUniqueId();
    if (fromId.equals(toId)) {
      var selfTargetMsg = snap.selfTarget();
      sender.sendError(selfTargetMsg);
      return;
    }

    var toLocation = to.getLocation();
    var fromName = from.getName();
    var toName = to.getName();
    var senderName = sender.name();
    var selfMove = Senders.isSelf(sender, from);

    var teleport = from.teleportAsync(toLocation);
    teleport.thenAccept(
        success ->
            onMoveComplete(success, snap, sender, from, fromName, toName, senderName, selfMove));
  }

  private void onMoveComplete(
      Boolean success,
      TeleportConfig snap,
      CommandActor sender,
      Player from,
      String fromName,
      String toName,
      String senderName,
      boolean selfMove) {
    if (!Boolean.TRUE.equals(success)) {
      var failedMsg = snap.teleportFailed();
      sender.sendError(failedMsg);
      return;
    }

    var senderMsg = snap.formatMoveSender(fromName, toName);
    sender.sendSuccess(senderMsg);

    if (selfMove) {
      return;
    }

    var fromActor = this.framework.actorOf(from);
    var notifyMsg = snap.formatMoveNotify(senderName);
    fromActor.sendSuccess(notifyMsg);
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
    var insideBorder = worldBorder.isInside(destination);
    if (y < minHeight || y >= maxHeight || !insideBorder) {
      var invalidMsg = snap.invalidPosition();
      senderActor.sendError(invalidMsg);
      return;
    }

    var teleport = sender.teleportAsync(destination);
    teleport.thenAccept(success -> onToPosComplete(success, snap, senderActor, x, y, z));
  }

  private void onToPosComplete(
      Boolean success,
      TeleportConfig snap,
      CommandActor senderActor,
      double x,
      double y,
      double z) {
    if (!Boolean.TRUE.equals(success)) {
      var failedMsg = snap.teleportFailed();
      senderActor.sendError(failedMsg);
      return;
    }

    var posMsg = snap.formatToPos(x, y, z);
    senderActor.sendSuccess(posMsg);
  }
}
