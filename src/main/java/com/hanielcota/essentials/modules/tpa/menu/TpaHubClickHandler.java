package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.command.TpaSendOrchestrator;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.domain.TpaContact;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaDndCycle;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.PlayerProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Handles the click actions for the dynamic slots in {@code TpaHelpMenu}: cancel an outgoing
 * request, cycle the DND preset, repeat a TPA to the last contacted player.
 */
@RequiredArgsConstructor
public final class TpaHubClickHandler {

  private final ConfigHandle<TpaConfig> config;
  private final TeleportRequestService requests;
  private final TpaProfileService profiles;
  private final PlayerProvider players;
  private final ActorFactory actors;
  private final TpaSendOrchestrator dispatcher;

  public void cancelOutgoing(@NonNull ClickContext click, @NonNull TeleportRequest request) {
    var actor = this.actors.actorOf(click.player());
    var messages = this.config.value().messages();

    this.requests.cancel(request);

    var targetName = request.target().name();
    var cancelledText = messages.cancelled().replace("{player}", targetName);
    actor.sendSuccess(cancelledText);

    click.refresh();
  }

  public void cycleDnd(@NonNull ClickContext click) {
    var viewer = click.player();
    var viewerId = viewer.getUniqueId();
    var profile = this.profiles.profile(viewerId);
    var now = System.currentTimeMillis();

    var currentStage = TpaDndCycle.stageOf(profile.dndUntilEpochMs(), now);
    var nextStage = currentStage.next();
    var nextUntil = TpaDndCycle.cycleTo(nextStage, now);

    this.profiles.setDndUntil(viewerId, nextUntil);
    click.refresh();
  }

  public void repeatLastContacted(@NonNull ClickContext click, @NonNull TpaContact contact) {
    var viewer = click.player();
    var actor = this.actors.actorOf(viewer);
    var snap = this.config.value();
    var messages = snap.messages();

    var resolved = this.players.online(contact.targetId());
    if (resolved.isEmpty()) {
      var offlineText = messages.favoriteOffline().replace("{player}", contact.targetName());
      actor.sendError(offlineText);
      return;
    }

    var target = resolved.get();
    var confirmationTemplate = messages.requestSent();
    var type = TeleportRequestType.TPA;

    click.close();
    this.dispatcher.send(actor, target, type, confirmationTemplate);
  }
}
