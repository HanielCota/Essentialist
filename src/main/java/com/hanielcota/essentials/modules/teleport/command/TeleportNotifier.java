package com.hanielcota.essentials.modules.teleport.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.teleport.config.TeleportConfig;
import com.hanielcota.essentials.modules.teleport.service.TeleportOutcome;
import com.hanielcota.essentials.paper.ActorFactory;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Owns every user-visible message emitted by the teleport commands ({@code /tp}, {@code /tp move},
 * {@code /tp pos}, {@code /tphere}). Maps the {@link TeleportOutcome} returned by {@link
 * com.hanielcota.essentials.modules.teleport.service.TeleportService} onto the matching dual /
 * single messages. Keeps the commands free of {@code .thenAccept} closures that bundle multiple
 * channels.
 */
@RequiredArgsConstructor
public final class TeleportNotifier {

  private final ConfigHandle<TeleportConfig> config;
  private final ActorFactory actors;

  public void notifyToPlayer(
      @NonNull CommandActor senderActor,
      @NonNull Player target,
      @NonNull String senderName,
      @NonNull String targetName,
      @NonNull TeleportOutcome outcome) {
    var snap = this.config.value();
    switch (outcome) {
      case SELF_TARGET -> senderActor.sendError(snap.selfTarget());
      case INVALID_POSITION -> senderActor.sendError(snap.invalidPosition());
      case FAILED -> senderActor.sendError(snap.teleportFailed());
      case SUCCESS -> {
        var targetActor = this.actors.actorOf(target);
        var senderMsg = snap.formatToPlayer(targetName);
        var targetMsg = snap.formatTeleportedTo(senderName);
        senderActor.sendDualMessage(targetActor, senderMsg, targetMsg);
      }
    }
  }

  public void notifyMove(
      @NonNull CommandActor sender,
      @NonNull Player from,
      @NonNull String fromName,
      @NonNull String toName,
      @NonNull String senderName,
      boolean selfMove,
      @NonNull TeleportOutcome outcome) {
    var snap = this.config.value();
    switch (outcome) {
      case SELF_TARGET -> sender.sendError(snap.selfTarget());
      case INVALID_POSITION -> sender.sendError(snap.invalidPosition());
      case FAILED -> sender.sendError(snap.teleportFailed());
      case SUCCESS -> {
        var senderMsg = snap.formatMoveSender(fromName, toName);
        sender.sendSuccess(senderMsg);

        if (selfMove) {
          return;
        }

        var fromActor = this.actors.actorOf(from);
        var notifyMsg = snap.formatMoveNotify(senderName);
        fromActor.sendSuccess(notifyMsg);
      }
    }
  }

  public void notifyToPos(
      @NonNull CommandActor senderActor,
      double x,
      double y,
      double z,
      @NonNull TeleportOutcome outcome) {
    var snap = this.config.value();
    switch (outcome) {
      case SELF_TARGET -> senderActor.sendError(snap.selfTarget());
      case INVALID_POSITION -> senderActor.sendError(snap.invalidPosition());
      case FAILED -> senderActor.sendError(snap.teleportFailed());
      case SUCCESS -> {
        var posMsg = snap.formatToPos(x, y, z);
        senderActor.sendSuccess(posMsg);
      }
    }
  }

  public void notifyBringHere(
      @NonNull CommandActor senderActor,
      @NonNull Player target,
      @NonNull String senderName,
      @NonNull String targetName,
      @NonNull TeleportOutcome outcome) {
    var snap = this.config.value();
    switch (outcome) {
      case SELF_TARGET -> senderActor.sendError(snap.selfTarget());
      case INVALID_POSITION -> senderActor.sendError(snap.invalidPosition());
      case FAILED -> senderActor.sendError(snap.teleportFailed());
      case SUCCESS -> {
        var targetActor = this.actors.actorOf(target);
        var senderMsg = snap.formatBroughtPlayer(targetName);
        var targetMsg = snap.formatBroughtBy(senderName);
        senderActor.sendDualMessage(targetActor, senderMsg, targetMsg);
      }
    }
  }
}
