package com.hanielcota.essentials.modules.tpa.command;

import com.hanielcota.essentials.modules.tpa.config.TpaMessages;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.util.Placeholders;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/** Shared command helpers — request lookup, sending and replying to the requester. */
final class TpaRequests {

  private TpaRequests() {}

  /**
   * Opens a new request from {@code sender} to {@code target}, refusing self-targets, and replies
   * to the sender. Used by {@code /tpa} and {@code /tpahere}.
   */
  static void send(
      TeleportRequestService service,
      TpaMessages messages,
      CommandActor actor,
      Player target,
      TeleportRequestType type,
      String confirmationTemplate) {
    Player sender = actor.unwrap(Player.class);
    if (sender.getUniqueId().equals(target.getUniqueId())) {
      actor.sendError(messages.selfTarget());
      return;
    }
    service.create(sender, target, type);
    actor.sendSuccess(Placeholders.format(confirmationTemplate, "player", target.getName()));
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
      TeleportRequestService service,
      Player viewer,
      String requesterName,
      TpaMessages messages,
      CommandActor actor) {

    if (!requesterName.isBlank()) {
      var found = service.incomingFrom(viewer.getUniqueId(), requesterName);
      if (found.isEmpty()) {
        actor.sendError(messages.noIncoming());
      }
      return found;
    }

    var pending = service.incoming(viewer.getUniqueId());
    if (pending.isEmpty()) {
      actor.sendError(messages.noIncoming());
      return Optional.empty();
    }
    if (pending.size() > 1) {
      actor.sendError(messages.ambiguous());
      return Optional.empty();
    }
    return Optional.of(pending.getFirst());
  }

  /**
   * Sends the requester their reply if they are still online, using {@code asSuccess} to pick the
   * success / error channel. No-ops when the requester has logged out.
   */
  static void replyRequester(
      PaperCommandFramework framework,
      TeleportRequest request,
      String template,
      boolean asSuccess) {
    var requesterPlayer = Bukkit.getPlayer(request.requester().id());
    if (requesterPlayer == null) {
      return;
    }
    var actor = framework.actorOf(requesterPlayer);
    var line = Placeholders.format(template, "player", request.target().name());
    if (asSuccess) {
      actor.sendSuccess(line);
    } else {
      actor.sendError(line);
    }
  }
}
