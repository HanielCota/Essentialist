package com.hanielcota.essentials.modules.online;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.online.command.OnlineCommand;
import com.hanielcota.essentials.modules.online.config.OnlineConfig;
import com.hanielcota.essentials.modules.vanish.service.VanishService;
import java.util.function.IntSupplier;
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
   * Live count of online players excluding vanished ones. Looks up {@link VanishService} on each
   * call so the wiring is independent of module load order — when the vanish module is disabled,
   * the lookup returns empty and the raw online count is used.
   */
  private IntSupplier visibleCount() {
    var registry = context().services();
    return () -> {
      var vanish = registry.find(VanishService.class).orElse(null);
      if (vanish == null) {
        return Bukkit.getOnlinePlayers().size();
      }
      var count = 0;
      for (var player : Bukkit.getOnlinePlayers()) {
        if (!vanish.isVanished(player.getUniqueId())) {
          count++;
        }
      }
      return count;
    };
  }
}
