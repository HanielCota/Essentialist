package com.hanielcota.essentials.modules.spawn;

import com.hanielcota.essentials.database.DefaultAsyncDatabaseWriter;
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
import com.hanielcota.essentials.modules.spawn.service.SpawnTable;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import java.util.Set;

/**
 * Server spawn point and the {@code /spawn} / {@code /setspawn} commands.
 *
 * <p>Persists the single spawn point in SQLite via {@link SpawnStore}; the SQL write is queued on
 * an {@link com.hanielcota.essentials.database.AsyncDatabaseWriter AsyncDatabaseWriter} so {@code
 * /setspawn} returns immediately. Warm-up and damage cancel logic comes from the shared {@link
 * DelayedTeleport} service registered by the {@code teleport} module.
 */
public final class SpawnModule extends AbstractModule {

  public SpawnModule() {
    super(new ModuleMetadata("spawn", Set.of("teleport"), "0.1.0", "Server spawn point."));
  }

  @Override
  protected void onEnable() {
    var config = config("spawn", SpawnConfig.class, SpawnConfig::defaults);
    var executor = service(SqlExecutor.class);
    SpawnTable.install(executor);

    var store = new SpawnStore(executor);
    var writer = new DefaultAsyncDatabaseWriter("Essentialist-Spawn");
    registerCloseable(writer);

    var spawnService = new SpawnService(store, writer);
    registerService(SpawnService.class, spawnService);

    var delayed = service(DelayedTeleport.class);

    var setSpawnCommand = new SetSpawnCommand(config, spawnService);
    var spawnCommand = new SpawnCommand(config, spawnService, delayed);
    registerCommand(setSpawnCommand);
    registerCommand(spawnCommand);

    var joinListener = new SpawnJoinListener(spawnService);
    var respawnListener = new SpawnRespawnListener(spawnService);
    var voidListener = new SpawnVoidListener(spawnService, delayed);
    registerListener(joinListener);
    registerListener(respawnListener);
    registerListener(voidListener);
  }
}
