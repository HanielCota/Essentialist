package com.hanielcota.essentials.modules.warps;

import com.hanielcota.essentials.database.DefaultAsyncDatabaseWriter;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleMetadata;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.modules.warps.command.DelWarpCommand;
import com.hanielcota.essentials.modules.warps.command.SetWarpCommand;
import com.hanielcota.essentials.modules.warps.command.WarpCommand;
import com.hanielcota.essentials.modules.warps.command.WarpPromptFactory;
import com.hanielcota.essentials.modules.warps.command.WarpsCommand;
import com.hanielcota.essentials.modules.warps.command.WarpsListNotifier;
import com.hanielcota.essentials.modules.warps.config.WarpsConfig;
import com.hanielcota.essentials.modules.warps.repository.WarpCache;
import com.hanielcota.essentials.modules.warps.repository.WarpStore;
import com.hanielcota.essentials.modules.warps.repository.WarpTable;
import com.hanielcota.essentials.modules.warps.service.WarpService;
import java.util.Set;

/**
 * Server warps: {@code /warp}, {@code /setwarp}, {@code /delwarp}, {@code /warps}.
 *
 * <p>Persists warps in SQLite via {@link WarpStore} with case-insensitive lookup by name. The full
 * set is loaded into {@link WarpCache} at module enable so {@code /warp} never hits SQL on the main
 * thread. Per-warp access is gated on the {@code essentials.warp.use.<name>} permission (or the
 * {@code essentials.warp.use.*} wildcard). Warm-up and damage cancel come from the shared {@link
 * DelayedTeleport} service.
 */
public final class WarpsModule extends AbstractModule {

  public WarpsModule() {
    super(new ModuleMetadata("warps", Set.of("teleport"), "0.1.0", "Server warps."));
  }

  @Override
  protected void onEnable() {
    var config = config("warps", WarpsConfig.class, WarpsConfig::defaults);
    var executor = service(SqlExecutor.class);
    WarpTable.install(executor);

    var store = new WarpStore(executor);
    var cache = new WarpCache();

    var existingWarps = store.list();
    cache.loadAll(existingWarps);

    var writer = new DefaultAsyncDatabaseWriter("Essentialist-Warps");
    registerCloseable(writer);

    var warpService = new WarpService(store, cache, writer);
    registerService(WarpService.class, warpService);

    var delayed = service(DelayedTeleport.class);

    var setWarpCommand = new SetWarpCommand(config, warpService);
    var promptFactory = new WarpPromptFactory();
    var warpCommand = new WarpCommand(config, warpService, delayed, promptFactory);
    var delWarpCommand = new DelWarpCommand(config, warpService);
    var warpsNotifier = new WarpsListNotifier(config);
    var warpsCommand = new WarpsCommand(warpService, warpsNotifier);

    registerCommand(setWarpCommand);
    registerCommand(warpCommand);
    registerCommand(delWarpCommand);
    registerCommand(warpsCommand);
  }
}
