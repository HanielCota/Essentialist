package com.hanielcota.essentials.modules.spawn;

import com.hanielcota.essentials.database.DefaultAsyncDatabaseWriter;
import com.hanielcota.essentials.database.SqlDialect;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleMetadata;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.spawn.command.SetSpawnCommand;
import com.hanielcota.essentials.modules.spawn.command.SpawnCommand;
import com.hanielcota.essentials.modules.spawn.config.SpawnConfig;
import com.hanielcota.essentials.modules.spawn.listener.SpawnJoinListener;
import com.hanielcota.essentials.modules.spawn.listener.SpawnRespawnListener;
import com.hanielcota.essentials.modules.spawn.listener.SpawnVoidListener;
import com.hanielcota.essentials.modules.spawn.repository.SpawnRepository;
import com.hanielcota.essentials.modules.spawn.repository.SpawnTable;
import com.hanielcota.essentials.modules.spawn.service.SpawnService;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import java.util.Set;
import lombok.NonNull;

/**
 * Server spawn point and the {@code /spawn} / {@code /setspawn} commands.
 *
 * <p>Persists the single spawn point in SQLite via {@link SpawnRepository}; the SQL write is queued
 * on an {@link com.hanielcota.essentials.database.AsyncDatabaseWriter AsyncDatabaseWriter} so
 * {@code /setspawn} returns immediately. Warm-up and damage cancel logic comes from the shared
 * {@link DelayedTeleport} service registered by the {@code teleport} module.
 */
public final class SpawnModule extends AbstractModule {

  public SpawnModule() {
    super(new ModuleMetadata("spawn", Set.of("teleport"), "0.1.0", "Server spawn point."));
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("spawn", SpawnConfig.class, SpawnConfig::defaults);
    var executor = env.service(SqlExecutor.class);
    var dialect = env.service(SqlDialect.class);
    var table = new SpawnTable(dialect);
    table.install(executor);

    var store = new SpawnRepository(executor, table);
    var writer = new DefaultAsyncDatabaseWriter("Essentialist-Spawn");
    registrar.closeable(writer);

    var spawnService = new SpawnService(store, writer);
    registrar.provide(SpawnService.class, spawnService);

    var delayed = env.service(DelayedTeleport.class);

    var setSpawnCommand = new SetSpawnCommand(config, spawnService);
    var spawnCommand = new SpawnCommand(config, spawnService, delayed);
    registrar.command(setSpawnCommand);
    registrar.command(spawnCommand);

    var joinListener = new SpawnJoinListener(spawnService);
    var respawnListener = new SpawnRespawnListener(spawnService);
    var voidListener = new SpawnVoidListener(spawnService, delayed);
    registrar.listener(joinListener);
    registrar.listener(respawnListener);
    registrar.listener(voidListener);
  }
}
