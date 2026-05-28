package com.hanielcota.essentials.modules.tpa.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.TpaMessages;
import com.hanielcota.essentials.modules.tpa.domain.AcceptOutcome;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import com.hanielcota.essentials.modules.tpa.service.favorites.TpaFavoriteService;
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
  private final TpAcceptOutcomeHandler acceptHandler;
  private final TpAcceptTeleportNotifier teleportNotifier;
  private final MainThreadCallbacks callbacks;
  private final ActorFactory actors;
  private final TpaNotifier notifier;

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
    var created = this.service.create(sender, target, type);
    if (created.isEmpty()) {
      handleRefusal(requesterActor, sender, target, senderId, targetId, type, messages);
      return;
    }

    var result = created.get();
    var replacedRequest = result.replacedRequest();
    if (replacedRequest != null) {
      this.notifier.notifyRequestReplaced(replacedRequest, senderId, sender.getName());
      this.notifier.notifyOutgoingReplaced(replacedRequest, target.getName());
    }

    var request = result.request();
    if (autoAccept) {
      autoAccept(request, target);
      return;
    }

    this.notifier.sendPrompt(target, request);

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

    if (claim != AcceptOutcome.ACCEPTED) {
      return;
    }

    var pending = this.service.dispatchTeleport(request);
    this.callbacks.hop(
        pending,
        success -> this.teleportNotifier.notifyOutcome(success, targetActor),
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
    var template = messages.disabledFor(type);
    sendError(requesterActor, template, target.getName());
  }
}
