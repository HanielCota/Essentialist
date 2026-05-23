package com.hanielcota.essentials.modules.homes;

import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleMetadata;
import com.hanielcota.essentials.modules.homes.command.DelHomeCommand;
import com.hanielcota.essentials.modules.homes.command.HomeCommand;
import com.hanielcota.essentials.modules.homes.command.HomesCommand;
import com.hanielcota.essentials.modules.homes.command.SetHomeCommand;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.service.HomeLimitResolver;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.modules.homes.service.HomeStore;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import java.util.Set;

/**
 * Per-player homes: {@code /sethome}, {@code /home}, {@code /delhome}, {@code /homes}.
 *
 * <p>Persists homes in SQLite via {@link HomeStore}; per-player limits come from {@link
 * HomeLimitResolver} which inspects {@code essentials.home.limit.N} permissions. Warm-up and
 * cancel-on-move logic comes from the shared {@link DelayedTeleport} service.
 */
public final class HomesModule extends AbstractModule {

  public HomesModule() {
    super(new ModuleMetadata("homes", Set.of("teleport"), "0.1.0", "Per-player homes."));
  }

  @Override
  protected void onEnable() {
    var config = config("homes", HomesConfig.class, HomesConfig::defaults);

    var store = new HomeStore(service(SqlExecutor.class));
    var limits = new HomeLimitResolver(config.value().defaultLimit());
    var homeService = new HomeService(store, limits);
    registerService(HomeService.class, homeService);

    var delayed = service(DelayedTeleport.class);

    registerCommand(new SetHomeCommand(config, homeService));
    registerCommand(new HomeCommand(config, homeService, delayed));
    registerCommand(new DelHomeCommand(config, homeService));
    registerCommand(new HomesCommand(config, homeService));
  }
}
