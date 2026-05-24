package com.hanielcota.essentials.modules.vanish.menu;

import com.github.hanielcota.menuframework.api.ClickContext;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.vanish.config.VanishConfig;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.Bukkit;

public record VanishClickHandler(ConfigHandle<VanishConfig> config) {

  public void handle(
      @NonNull ClickContext click, @NonNull UUID targetId, @NonNull String targetName) {
    var snap = this.config.value();
    var target = Bukkit.getPlayer(targetId);

    if (target == null) {
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

    click.close();

    if (!click.player().teleport(location)) {
      click.reply(snap.teleportFailed());
      return;
    }

    var teleportedMsg =
        snap.formatTeleported(
            target.getName(), world.getName(), location.getX(), location.getY(), location.getZ());
    click.reply(teleportedMsg);
  }
}
