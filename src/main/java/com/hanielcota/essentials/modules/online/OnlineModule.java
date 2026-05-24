package com.hanielcota.essentials.modules.online;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.online.command.OnlineCommand;
import com.hanielcota.essentials.modules.online.config.OnlineConfig;
import com.hanielcota.essentials.modules.vanish.service.VanishService;
import com.hanielcota.essentials.modules.vanish.service.VanishVisibilityApplier;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.function.ToIntFunction;
import org.bukkit.Bukkit;

public final class OnlineModule extends AbstractModule {

  public OnlineModule() {
    super("online");
  }

  @Override
  protected void onEnable() {
    var config = config("online", OnlineConfig.class, OnlineConfig::defaults);
    registerCommand(new OnlineCommand(config, visibleCount()));
  }

  /**
   * Counts online players, excluding vanished ones for viewers without {@link
   * VanishVisibilityApplier#SEE_PERMISSION}. Staff with the see-perm get the true count. Looks up
   * {@link VanishService} on each call so module load order does not matter — if vanish is disabled
   * the raw count is returned.
   */
  private ToIntFunction<CommandActor> visibleCount() {
    var registry = context().services();
    return actor -> {
      var online = Bukkit.getOnlinePlayers();
      var vanish = registry.find(VanishService.class).orElse(null);
      if (vanish == null || actor.hasPermission(VanishVisibilityApplier.SEE_PERMISSION)) {
        return online.size();
      }
      var count = 0;
      for (var player : online) {
        if (!vanish.isVanished(player.getUniqueId())) {
          count++;
        }
      }
      return count;
    };
  }
}
