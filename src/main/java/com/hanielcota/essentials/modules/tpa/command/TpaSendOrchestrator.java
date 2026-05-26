package com.hanielcota.essentials.modules.tpa.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.TpaMessages;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.service.AcceptResult;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteService;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Unifies the "send a TPA" entry point used by {@code /tpa}, {@code /tpahere}, the favorites action
 * menu and the hub's last-contacted shortcut. Picks the right refusal message when {@link
 * TeleportRequestService#create} returns empty, and auto-accepts when the target has the toggle on
 * and the requester is in their favorites list.
 */
@RequiredArgsConstructor
public final class TpaSendOrchestrator {

  private final ConfigHandle<TpaConfig> config;
  private final TeleportRequestService service;
  private final TpaFavoriteService favorites;
  private final TpaProfileService profiles;
  private final TpAcceptResultHandler acceptHandler;
  private final MainThreadCallbacks callbacks;
  private final ActorFactory actors;

  private static void sendError(
      @NonNull CommandActor actor, @NonNull String template, @NonNull String playerName) {
    var msg = template.replace("{player}", playerName);
    actor.sendError(msg);
  }

  public void send(
      @NonNull CommandActor requesterActor,
      @NonNull Player target,
      @NonNull TeleportRequestType type,
      @NonNull String confirmationTemplate) {
    var sender = requesterActor.unwrap(Player.class);
    var senderId = sender.getUniqueId();
    var targetId = target.getUniqueId();
    var snap = this.config.value();
    var messages = snap.messages();

    if (senderId.equals(targetId)) {
      requesterActor.sendError(messages.selfTarget());
      return;
    }

    var autoAccept = shouldAutoAccept(targetId, senderId);
    var created = this.service.create(sender, target, type, !autoAccept);
    if (created.isEmpty()) {
      handleRefusal(requesterActor, sender, target, senderId, targetId, type, messages);
      return;
    }

    var request = created.get();
    if (autoAccept) {
      autoAccept(request, target);
      return;
    }

    var confirmationMsg = confirmationTemplate.replace("{player}", target.getName());
    requesterActor.sendSuccess(confirmationMsg);
  }

  private boolean shouldAutoAccept(@NonNull UUID targetId, @NonNull UUID senderId) {
    var targetProfile = this.profiles.profile(targetId);
    if (!targetProfile.autoAcceptFavorites()) {
      return false;
    }
    return this.favorites.isFavorite(targetId, senderId);
  }

  private void autoAccept(@NonNull TeleportRequest request, @NonNull Player target) {
    var targetActor = this.actors.actorOf(target);
    var claim = this.service.tryAccept(request);
    this.acceptHandler.handleClaim(claim, request, targetActor);

    if (claim != AcceptResult.ACCEPTED) {
      return;
    }

    var pending = this.service.dispatchTeleport(request);
    this.callbacks.hop(
        pending,
        success -> this.acceptHandler.handleTeleportOutcome(success, targetActor),
        "tpa auto-accept");
  }

  private void handleRefusal(
      @NonNull CommandActor requesterActor,
      @NonNull Player sender,
      @NonNull Player target,
      @NonNull UUID senderId,
      @NonNull UUID targetId,
      @NonNull TeleportRequestType type,
      @NonNull TpaMessages messages) {
    if (this.service.isBlockedBy(targetId, senderId)) {
      sendError(requesterActor, messages.blockedByPlayer(), target.getName());
      return;
    }
    if (this.service.isDndActive(targetId)) {
      sendError(requesterActor, messages.dndActive(), target.getName());
      return;
    }
    if (this.service.isCrossWorldRefused(sender, target)) {
      sendError(requesterActor, messages.crossWorldRefused(), target.getName());
      return;
    }
    var template =
        type == TeleportRequestType.TPA ? messages.tpaDisabled() : messages.tpaHereDisabled();
    sendError(requesterActor, template, target.getName());
  }
}
