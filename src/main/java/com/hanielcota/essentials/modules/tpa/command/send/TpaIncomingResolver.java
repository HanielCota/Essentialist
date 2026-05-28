package com.hanielcota.essentials.modules.tpa.command.send;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.service.request.TeleportRequestService;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Resolves the incoming request the viewer wants to act on. A blank {@code requesterName} resolves
 * the sole pending request (or reports "none" / "ambiguous"); a given name resolves that specific
 * request (or reports "none"). The matching error message is sent to {@code actor} on failure.
 */
@RequiredArgsConstructor
public final class TpaIncomingResolver {

  private final ConfigHandle<TpaConfig> config;
  private final TeleportRequestService service;

  public Optional<TeleportRequest> resolve(
      @NonNull Player viewer, @NonNull String requesterName, @NonNull CommandActor actor) {
    var snap = this.config.value();
    var messages = snap.messages();
    var viewerId = viewer.getUniqueId();

    if (!requesterName.isBlank()) {
      var found = this.service.incomingFrom(viewerId, requesterName);
      if (found.isEmpty()) {
        actor.sendError(messages.noIncoming());
      }
      return found;
    }

    var pending = this.service.incoming(viewerId);
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
}
