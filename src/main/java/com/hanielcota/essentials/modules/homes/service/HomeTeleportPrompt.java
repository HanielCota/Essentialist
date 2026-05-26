package com.hanielcota.essentials.modules.homes.service;

import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.util.ClickableMessage;
import com.hanielcota.essentials.util.ClickableMessageSegment;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Chat feedback for the homes warm-up teleport. Same lifecycle wiring as the shared {@code
 * DelayedTeleportPrompt}, but the countdown line is rendered as a clickable component with a {@code
 * [Cancel]} suffix that runs {@code /tpcancel}.
 */
@RequiredArgsConstructor
public final class HomeTeleportPrompt implements DelayedTeleport.Callback {

  private static final String CANCEL_COMMAND = "/tpcancel";

  private final CommandActor actor;
  private final Player player;
  private final String teleporting;
  private final String teleported;
  private final String cancelled;
  private final String failed;
  private final String cancelButton;
  private final String cancelHover;

  @Override
  public void onScheduled(long seconds) {
    if (seconds <= 0) {
      return;
    }

    var secondsStr = Long.toString(seconds);
    var countdownMsg = this.teleporting.replace("{seconds}", secondsStr);

    var hover = this.cancelHover;
    Consumer<ClickableMessageSegment> cancelSegment =
        segment -> segment.runCommand(CANCEL_COMMAND).hover(hover);

    var message = ClickableMessage.create();
    message.append(countdownMsg);
    message.space();
    message.append(this.cancelButton, cancelSegment);

    message.send(this.player);
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
