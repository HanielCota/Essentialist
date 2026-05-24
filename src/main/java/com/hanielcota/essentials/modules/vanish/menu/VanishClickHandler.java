package com.hanielcota.essentials.modules.vanish.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.vanish.config.VanishConfig;
import com.hanielcota.essentials.modules.vanish.service.VanishService;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.Bukkit;

/**
 * Teleports the viewer to a vanished player. Re-checks {@link VanishService#isVanished(UUID)} at
 * click time so a head that lingers in the rendered slot after the target retoggles never sends the
 * viewer to a now-visible player.
 */
public record VanishClickHandler(ConfigHandle<VanishConfig> config, VanishService service) {

  public void handle(
      @NonNull ClickContext click, @NonNull UUID targetId, @NonNull String targetName) {
    var snap = this.config.value();
    var target = Bukkit.getPlayer(targetId);

    if (target == null || !this.service.isVanished(targetId)) {
      click.reply(snap.formatTeleportTargetGone(targetName));
      click.refresh();
      return;
    }

    var location = target.getLocation();
    var world = location.getWorld();
    if (world == null) {
      click.reply(snap.teleportFailed());
      return;
    }

    var viewer = click.player();
    var worldName = world.getName();
    var x = location.getX();
    var y = location.getY();
    var z = location.getZ();
    var name = target.getName();

    click.close();
    viewer
        .teleportAsync(location)
        .thenAccept(
            success -> {
              if (Boolean.TRUE.equals(success)) {
                click.reply(snap.formatTeleported(name, worldName, x, y, z));
              } else {
                click.reply(snap.teleportFailed());
              }
            });
  }
}
