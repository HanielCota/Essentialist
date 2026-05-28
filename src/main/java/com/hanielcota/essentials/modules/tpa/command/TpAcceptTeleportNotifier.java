package com.hanielcota.essentials.modules.tpa.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * One-shot notifier for the deferred teleport outcome that follows a {@code /tpaccept} claim.
 * Separated from {@link TpAcceptOutcomeHandler} so the synchronous claim routing and the async
 * teleport reporting stay in different classes.
 */
@RequiredArgsConstructor
public final class TpAcceptTeleportNotifier {

  private final @NonNull ConfigHandle<TpaConfig> config;

  public void notifyOutcome(boolean success, @NonNull CommandActor actor) {
    if (success) {
      return;
    }

    var snap = this.config.value();
    var messages = snap.messages();

    actor.sendError(messages.teleportFailed());
  }
}
