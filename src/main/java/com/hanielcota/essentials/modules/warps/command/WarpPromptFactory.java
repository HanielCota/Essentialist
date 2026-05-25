package com.hanielcota.essentials.modules.warps.command;

import com.hanielcota.essentials.modules.teleport.service.DelayedTeleportPrompt;
import com.hanielcota.essentials.modules.warps.config.WarpsMessages;
import com.hanielcota.essentials.modules.warps.domain.Warp;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;

/**
 * Builds the {@link DelayedTeleportPrompt} for {@code /warp}: pre-fills {@code {name}} on the
 * teleporting / teleported templates so the prompt only needs to fill {@code {seconds}} at runtime.
 */
public final class WarpPromptFactory {

  private static final String NAME = "{name}";

  public DelayedTeleportPrompt create(
      @NonNull CommandActor actor, @NonNull WarpsMessages messages, @NonNull Warp warp) {
    var warpName = warp.name();

    var teleportingTemplate = messages.teleporting();
    var teleportingMsg = teleportingTemplate.replace(NAME, warpName);

    var teleportedTemplate = messages.teleported();
    var teleportedMsg = teleportedTemplate.replace(NAME, warpName);

    var cancelledMsg = messages.cancelled();
    var failedMsg = messages.failed();

    return new DelayedTeleportPrompt(actor, teleportingMsg, teleportedMsg, cancelledMsg, failedMsg);
  }
}
