package com.hanielcota.essentials.modules.spawn;

import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleMetadata;
import com.hanielcota.essentials.modules.spawn.command.SetSpawnCommand;
import com.hanielcota.essentials.modules.spawn.command.SpawnCommand;
import com.hanielcota.essentials.modules.spawn.config.SpawnConfig;
import com.hanielcota.essentials.modules.spawn.listener.SpawnJoinListener;
import com.hanielcota.essentials.modules.spawn.listener.SpawnRespawnListener;
import com.hanielcota.essentials.modules.spawn.listener.SpawnVoidListener;
import com.hanielcota.essentials.modules.spawn.service.SpawnService;
import com.hanielcota.essentials.modules.spawn.service.SpawnStore;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import java.util.Set;

/**
 * Server spawn point and the {@code /spawn} / {@code /setspawn} commands.
 *
 * <p>Persists the single spawn point in SQLite via {@link SpawnStore}; warm-up and cancel-on-move
 * logic comes from the shared {@link DelayedTeleport} service registered by the {@code teleport}
 * module.
 */
public final class SpawnModule extends AbstractModule {

  public SpawnModule() {
    super(new ModuleMetadata("spawn", Set.of("teleport"), "0.1.0", "Server spawn point."));
  }

  @Override
  protected void onEnable() {
    var config = config("spawn", SpawnConfig.class, SpawnConfig::defaults);
    var executor = service(SqlExecutor.class);
    SpawnStore.install(executor);

    var store = new SpawnStore(executor);
    var spawnService = new SpawnService(store);
    registerService(SpawnService.class, spawnService);

    var delayed = service(DelayedTeleport.class);

    var setSpawnCommand = new SetSpawnCommand(config, spawnService);
    registerCommand(setSpawnCommand);

    var spawnCommand = new SpawnCommand(config, spawnService, delayed);
    registerCommand(spawnCommand);

    var joinListener = new SpawnJoinListener(spawnService);
    registerListener(joinListener);

    var respawnListener = new SpawnRespawnListener(spawnService);
    registerListener(respawnListener);

    var voidListener = new SpawnVoidListener(spawnService);
    registerListener(voidListener);
  }
}
