package com.hanielcota.essentials.modules.tpa.command;

import com.hanielcota.essentials.modules.tpa.config.TpaMessages;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/** Shared command helpers — request lookup, sending and replying to the requester. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class TpaRequests {

  /**
   * Opens a new request from {@code sender} to {@code target}, refusing self-targets, and replies
   * to the sender. Used by {@code /tpa} and {@code /tpahere}.
   */
  static void send(
      @NonNull TeleportRequestService service,
      @NonNull TpaMessages messages,
      @NonNull CommandActor actor,
      @NonNull Player target,
      @NonNull TeleportRequestType type,
      @NonNull String confirmationTemplate) {
    var sender = actor.unwrap(Player.class);
    var senderId = sender.getUniqueId();
    var targetId = target.getUniqueId();

    if (senderId.equals(targetId)) {
      actor.sendError(messages.selfTarget());
      return;
    }

    service.create(sender, target, type);

    var targetName = target.getName();
    var confirmationMsg = confirmationTemplate.replace("{player}", targetName);

    actor.sendSuccess(confirmationMsg);
  }

  /**
   * Resolves the incoming request the viewer wants to act on, sending the matching error message
   * itself when the choice cannot be made.
   *
   * <ul>
   *   <li>A blank {@code requesterName} resolves the sole pending request, or reports "none" /
   *       "ambiguous".
   *   <li>A given name resolves that specific request, or reports "none".
   * </ul>
   */
  static Optional<TeleportRequest> resolveIncoming(
      @NonNull TeleportRequestService service,
      @NonNull Player viewer,
      @NonNull String requesterName,
      @NonNull TpaMessages messages,
      @NonNull CommandActor actor) {
    var viewerId = viewer.getUniqueId();

    if (!requesterName.isBlank()) {
      var found = service.incomingFrom(viewerId, requesterName);
      if (found.isEmpty()) {
        actor.sendError(messages.noIncoming());
      }
      return found;
    }

    var pending = service.incoming(viewerId);
    if (pending.isEmpty()) {
      actor.sendError(messages.noIncoming());
      return Optional.empty();
    }
    if (pending.size() > 1) {
      actor.sendError(messages.ambiguous());
      return Optional.empty();
    }

    var sole = pending.getFirst();
    return Optional.of(sole);
  }

  /**
   * Sends the requester their reply if they are still online, using {@code asSuccess} to pick the
   * success / error channel. No-ops when the requester has logged out.
   */
  static void replyRequester(
      @NonNull PaperCommandFramework framework,
      @NonNull TeleportRequest request,
      @NonNull String template,
      boolean asSuccess) {
    var requesterId = request.requester().id();
    var requesterPlayer = Bukkit.getPlayer(requesterId);
    if (requesterPlayer == null) {
      return;
    }

    var actor = framework.actorOf(requesterPlayer);

    var replyTargetName = request.target().name();
    var replyMsg = template.replace("{player}", replyTargetName);

    if (!asSuccess) {
      actor.sendError(replyMsg);
      return;
    }
    actor.sendSuccess(replyMsg);
  }
}
