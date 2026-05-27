package com.hanielcota.essentials.modules.spawn.command;

import com.hanielcota.essentials.modules.spawn.config.SpawnMessages;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleportPrompt;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;

/**
 * Builds the {@link DelayedTeleportPrompt} for {@code /spawn}: extracts the four message variables
 * from {@link SpawnMessages} so the command stays thin. Mirrors {@link
 * com.hanielcota.essentials.modules.warps.command.WarpPromptFactory}.
 */
public final class SpawnPromptFactory {

  private SpawnPromptFactory() {}

  public static DelayedTeleportPrompt create(
      @NonNull CommandActor actor, @NonNull SpawnMessages messages) {
    var teleportingMsg = messages.teleporting();
    var teleportedMsg = messages.teleported();
    var cancelledMsg = messages.cancelled();
    var failedMsg = messages.failed();

    return new DelayedTeleportPrompt(actor, teleportingMsg, teleportedMsg, cancelledMsg, failedMsg);
  }
}
