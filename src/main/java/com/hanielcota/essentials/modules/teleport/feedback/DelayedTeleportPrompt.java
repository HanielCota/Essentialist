package com.hanielcota.essentials.modules.teleport.feedback;

import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import io.github.hanielcota.commandframework.core.CommandActor;

/**
 * Standard chat feedback for a {@link DelayedTeleport} warm-up.
 *
 * <p>Sole responsibility: route each lifecycle hook to the right {@link CommandActor} channel.
 * Templates are pre-formatted by the caller (e.g. with {@code {name}} already resolved); this class
 * only fills the {@code {seconds}} placeholder of {@link #teleporting}.
 */
public record DelayedTeleportPrompt(
    CommandActor actor, String teleporting, String teleported, String cancelled, String failed)
    implements DelayedTeleport.Callback {

  @Override
  public void onScheduled(long seconds) {
    if (seconds <= 0) {
      return;
    }

    var countdownMsg = this.teleporting.replace("{seconds}", Long.toString(seconds));
    this.actor.sendMessage(countdownMsg);
  }

  @Override
  public void onSuccess() {
    this.actor.sendSuccess(this.teleported);
  }

  @Override
  public void onCancelled() {
    this.actor.sendError(this.cancelled);
  }

  @Override
  public void onFailed() {
    this.actor.sendError(this.failed);
  }
}
