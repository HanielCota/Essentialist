package com.hanielcota.essentials.modules.tpa.command;

import com.hanielcota.essentials.modules.tpa.config.TpaMessages;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.Optional;
import org.bukkit.entity.Player;

/** Shared lookup used by {@code /tpaccept} and {@code /tpdeny} to pick the request to act on. */
final class TpaRequests {

  private TpaRequests() {}

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
}
