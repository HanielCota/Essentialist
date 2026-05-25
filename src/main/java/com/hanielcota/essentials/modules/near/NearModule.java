package com.hanielcota.essentials.modules.near;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.near.command.NearCommand;
import com.hanielcota.essentials.modules.near.command.NearResultFormatter;
import com.hanielcota.essentials.modules.near.config.NearConfig;
import com.hanielcota.essentials.modules.near.service.NearService;
import com.hanielcota.essentials.modules.vanish.service.VanishService;
import com.hanielcota.essentials.modules.vanish.service.VanishVisibilityApplier;
import java.util.function.BiPredicate;
import lombok.NonNull;
import org.bukkit.entity.Player;

public final class NearModule extends AbstractModule {

  public NearModule() {
    super("near");
  }

  @Override
  protected void onEnable() {
    var config = config("near", NearConfig.class, NearConfig::defaults);
    var filter = visibilityFilter();
    var service = new NearService(filter);
    var formatter = new NearResultFormatter();
    var command = new NearCommand(config, service, formatter);

    registerCommand(command);
  }

  /**
   * Predicate that hides vanished players from /near results — unless the requesting viewer has
   * {@link VanishVisibilityApplier#SEE_PERMISSION}, in which case all players are visible. Looks up
   * {@link VanishService} on each call so module load order between vanish and near does not
   * matter; if vanish is disabled, every player is visible.
   */
  private BiPredicate<Player, Player> visibilityFilter() {
    var registry = context().services();
    return (viewer, target) -> {
      var vanish = registry.find(VanishService.class).orElse(null);
      return canSee(vanish, viewer, target);
    };
  }

  private static boolean canSee(
      VanishService vanish, @NonNull Player viewer, @NonNull Player target) {
    if (vanish == null) {
      return true;
    }
    if (viewer.hasPermission(VanishVisibilityApplier.SEE_PERMISSION)) {
      return true;
    }
    var targetId = target.getUniqueId();
    return !vanish.isVanished(targetId);
  }
}
