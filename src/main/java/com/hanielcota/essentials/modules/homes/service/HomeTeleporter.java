package com.hanielcota.essentials.modules.homes.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.config.messages.HomesMessages;
import com.hanielcota.essentials.modules.homes.domain.Home;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Encapsulates the warm-up teleport flow used by both {@code /home} and the /homes menu: resolve
 * the home's location, build the clickable {@link HomeTeleportPrompt} from the configured chat
 * lines, and hand off to {@link DelayedTeleport}. Reports {@code worldGone} to the caller's actor
 * when the home's world is unloaded.
 */
@RequiredArgsConstructor
public final class HomeTeleporter {

  private final ConfigHandle<HomesConfig> config;
  private final DelayedTeleport delayed;

  private static HomeTeleportPrompt buildPrompt(
      @NonNull Player player,
      @NonNull CommandActor actor,
      @NonNull HomesMessages messages,
      @NonNull Home home) {

    var homeName = home.name();

    var teleportingTemplate = messages.teleporting();
    var teleportedTemplate = messages.teleported();

    var teleportingMsg = teleportingTemplate.replace("{name}", homeName);
    var teleportedMsg = teleportedTemplate.replace("{name}", homeName);

    var cancelledMsg = messages.cancelled();
    var failedMsg = messages.failed();
    var cancelButton = messages.cancelButton();
    var cancelHover = messages.cancelHover();

    return new HomeTeleportPrompt(
        actor,
        player,
        teleportingMsg,
        teleportedMsg,
        cancelledMsg,
        failedMsg,
        cancelButton,
        cancelHover);
  }

  public void teleport(@NonNull Player player, @NonNull Home home, @NonNull CommandActor actor) {
    var snap = this.config.value();
    var messages = snap.messages();
    var resolved = home.resolve().orElse(null);
    if (resolved == null) {
      var worldGoneMsg = messages.worldGone();
      actor.sendError(worldGoneMsg);
      return;
    }

    var delay = snap.teleportDelay();
    var prompt = buildPrompt(player, actor, messages, home);

    this.delayed.schedule(player, resolved, delay, prompt);
  }
}
