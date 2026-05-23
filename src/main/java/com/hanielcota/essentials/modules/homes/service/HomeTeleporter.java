package com.hanielcota.essentials.modules.homes.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.config.HomesMessages;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleportPrompt;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Encapsulates the warm-up teleport flow used by both {@code /home} and the /homes menu: resolve
 * the home's location, build the standard {@link DelayedTeleportPrompt} from the configured chat
 * lines, and hand off to {@link DelayedTeleport}. Reports {@code worldGone} to the caller's actor
 * when the home's world is unloaded.
 */
@RequiredArgsConstructor
public final class HomeTeleporter {

  private final ConfigHandle<HomesConfig> config;
  private final DelayedTeleport delayed;
  private final PaperCommandFramework framework;

  public void teleport(Player player, Home home, CommandActor actor) {
    var snap = config.value();
    var messages = snap.messages();
    var resolved = home.resolve();

    if (resolved.isEmpty()) {
      actor.sendError(messages.worldGone());
      return;
    }

    delayed.schedule(
        player, resolved.get(), snap.teleportDelay(), buildPrompt(actor, messages, home));
  }

  public void teleport(Player player, Home home) {
    teleport(player, home, framework.actorOf(player));
  }

  private static DelayedTeleportPrompt buildPrompt(
      CommandActor actor, HomesMessages messages, Home home) {
    return new DelayedTeleportPrompt(
        actor,
        messages.teleporting().replace("{name}", home.name()),
        messages.teleported().replace("{name}", home.name()),
        messages.cancelled(),
        messages.failed());
  }
}
