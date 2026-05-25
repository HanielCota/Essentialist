package com.hanielcota.essentials.modules.vanish.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.vanish.config.VanishConfig;
import com.hanielcota.essentials.modules.vanish.config.VanishMessages;
import com.hanielcota.essentials.modules.vanish.service.VanishService;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.NonNull;

/**
 * Teleports the viewer to a vanished player. Re-checks {@link VanishService#isVanished(UUID)} at
 * click time so a head that lingers in the rendered slot after the target retoggles never sends the
 * viewer to a now-visible player.
 */
public record VanishClickHandler(
    ConfigHandle<VanishConfig> config,
    VanishService service,
    PlayerProvider players,
    MainThreadCallbacks callbacks) {

  private static Consumer<Boolean> afterTeleport(
      @NonNull ClickContext click, @NonNull String teleportedMsg, @NonNull String failedMsg) {
    return success -> {
      if (Boolean.TRUE.equals(success)) {
        click.reply(teleportedMsg);
        return;
      }
      click.reply(failedMsg);
    };
  }

  public void handle(
      @NonNull ClickContext click, @NonNull UUID targetId, @NonNull String targetName) {
    var snap = this.config.value();
    var target = this.players.online(targetId).orElse(null);

    if (target == null || !this.service.isVanished(targetId)) {
      var goneMsg = VanishMessages.teleportTargetGone(snap, targetName);
      click.reply(goneMsg);
      click.refresh();
      return;
    }

    var location = target.getLocation();
    var world = location.getWorld();
    if (world == null) {
      var failedMsg = snap.teleportFailed();
      click.reply(failedMsg);
      return;
    }

    var viewer = click.player();
    var worldName = world.getName();
    var x = location.getX();
    var y = location.getY();
    var z = location.getZ();
    var name = target.getName();

    var teleportedMsg = VanishMessages.teleported(snap, name, worldName, x, y, z);
    var failedMsg = snap.teleportFailed();
    var afterTeleport = afterTeleport(click, teleportedMsg, failedMsg);

    click.close();

    var future = viewer.teleportAsync(location);
    this.callbacks.hop(future, afterTeleport, "vanish teleport");
  }
}
