package com.hanielcota.essentials.modules.warps.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.modules.warps.command.WarpPromptFactory;
import com.hanielcota.essentials.modules.warps.config.WarpsConfig;
import com.hanielcota.essentials.modules.warps.domain.Warp;
import com.hanielcota.essentials.paper.ActorFactory;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Single teleport path for warps, used by the menu click. Validates the per-warp permission,
 * resolves the world, schedules the warm-up teleport with the standard chat prompt, and records the
 * player in {@link WarpOccupancy} once they arrive. Permission/world-gone feedback is sent here so
 * the menu click handler stays thin.
 */
@RequiredArgsConstructor
public final class WarpTeleportService {

  private static final String NAME = "{name}";

  private final ConfigHandle<WarpsConfig> config;
  private final WarpService service;
  private final WarpResolver resolver;
  private final DelayedTeleport delayed;
  private final WarpPromptFactory promptFactory;
  private final WarpOccupancy occupancy;
  private final ActorFactory actorFactory;

  public void teleport(@NonNull Player player, @NonNull Warp warp) {
    var snap = this.config.value();
    var messages = snap.messages();
    var actor = this.actorFactory.actorOf(player);
    var warpName = warp.name();

    if (!this.service.canUse(player, warpName)) {
      var noPermMsg = messages.noPermission().replace(NAME, warpName);
      actor.sendError(noPermMsg);
      return;
    }

    var locationOpt = this.resolver.resolve(warp);
    if (locationOpt.isEmpty()) {
      actor.sendError(messages.worldGone());
      return;
    }

    var destination = locationOpt.get();
    var delay = snap.teleportDelay();
    var prompt = this.promptFactory.create(actor, messages, warp);

    var playerId = player.getUniqueId();
    var callback = new WarpArrivalCallback(prompt, this.occupancy, playerId, warp, destination);

    this.delayed.schedule(player, destination, delay, callback);
  }
}
