package com.hanielcota.essentials.modules.tpa.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.command.TpaNotifier;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.paper.ActorFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Handles the click action on the dynamic outgoing-request slot in {@code TpaHelpMenu}: cancelling
 * the viewer's pending /tpa.
 */
@RequiredArgsConstructor
public final class TpaHubClickHandler {

  private final ConfigHandle<TpaConfig> config;
  private final TeleportRequestService requests;
  private final TpaNotifier notifier;
  private final ActorFactory actors;

  public void cancelOutgoing(@NonNull ClickContext click, @NonNull TeleportRequest request) {
    var actor = this.actors.actorOf(click.player());
    var messages = this.config.value().messages();

    var cancelled = this.requests.cancel(request);
    if (!cancelled) {
      actor.sendError(messages.noOutgoing());
      click.refresh();
      return;
    }

    this.notifier.notifyCancelledByRequester(request);

    var targetName = request.target().name();
    var cancelledText = messages.cancelled().replace("{player}", targetName);
    actor.sendSuccess(cancelledText);

    click.refresh();
  }
}
