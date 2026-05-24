package com.hanielcota.essentials.modules.near;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.near.command.NearCommand;
import com.hanielcota.essentials.modules.near.config.NearConfig;
import com.hanielcota.essentials.modules.near.service.NearService;
import com.hanielcota.essentials.modules.vanish.service.VanishService;
import java.util.function.Predicate;
import org.bukkit.entity.Player;

public final class NearModule extends AbstractModule {

  public NearModule() {
    super("near");
  }

  @Override
  protected void onEnable() {
    var config = config("near", NearConfig.class, NearConfig::defaults);
    registerCommand(new NearCommand(config, new NearService(visibilityFilter())));
  }

  /**
   * Predicate that hides vanished players from /near results. Looks up {@link VanishService} on
   * each call so load order between the modules does not matter; if vanish is disabled, every
   * player is visible.
   */
  private Predicate<Player> visibilityFilter() {
    var registry = context().services();
    return player -> {
      var vanish = registry.find(VanishService.class).orElse(null);
      if (vanish == null) {
        return true;
      }
      return !vanish.isVanished(player.getUniqueId());
    };
  }
}
