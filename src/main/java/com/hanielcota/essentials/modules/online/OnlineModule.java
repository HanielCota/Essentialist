package com.hanielcota.essentials.modules.online;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.online.command.OnlineCommand;
import com.hanielcota.essentials.modules.online.config.OnlineConfig;
import com.hanielcota.essentials.modules.vanish.service.VanishService;
import com.hanielcota.essentials.modules.vanish.service.VanishVisibilityApplier;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import lombok.NonNull;
import org.bukkit.entity.Player;

public final class OnlineModule extends AbstractModule {

  public OnlineModule() {
    super("online");
  }

  /**
   * Counts online players, excluding vanished ones for viewers without {@link
   * VanishVisibilityApplier#SEE_PERMISSION}. Staff with the see-perm get the true count. The vanish
   * service is supplied lazily so module load order does not matter — if vanish is disabled the raw
   * count is returned.
   */
  private static ToIntFunction<CommandActor> visibleCount(
      @NonNull PlayerProvider players, @NonNull Supplier<Optional<VanishService>> vanishService) {
    return actor -> {
      var vanish = vanishService.get().orElse(null);
      return countFor(vanish, actor, players);
    };
  }

  private static int countFor(
      VanishService vanish, @NonNull CommandActor actor, @NonNull PlayerProvider players) {
    var online = players.all();

    if (vanish == null) {
      return online.size();
    }
    if (actor.hasPermission(VanishVisibilityApplier.SEE_PERMISSION)) {
      return online.size();
    }

    return countVisible(online, vanish);
  }

  private static int countVisible(
      @NonNull Iterable<? extends Player> online, @NonNull VanishService vanish) {
    var count = 0;
    for (var player : online) {
      var playerId = player.getUniqueId();
      if (vanish.isVanished(playerId)) {
        continue;
      }
      count++;
    }

    return count;
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("online", OnlineConfig.class, OnlineConfig::defaults);
    var players = env.service(PlayerProvider.class);
    Supplier<Optional<VanishService>> vanishService = () -> env.findService(VanishService.class);
    var visibleCount = visibleCount(players, vanishService);
    var command = new OnlineCommand(config, players, visibleCount);

    registrar.command(command);
  }
}
