package com.hanielcota.essentials.modules.warps;

import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleMetadata;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.modules.warps.command.DelWarpCommand;
import com.hanielcota.essentials.modules.warps.command.SetWarpCommand;
import com.hanielcota.essentials.modules.warps.command.WarpCommand;
import com.hanielcota.essentials.modules.warps.command.WarpsCommand;
import com.hanielcota.essentials.modules.warps.config.WarpsConfig;
import com.hanielcota.essentials.modules.warps.service.WarpService;
import com.hanielcota.essentials.modules.warps.service.WarpStore;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.Set;

/**
 * Server warps: {@code /warp}, {@code /setwarp}, {@code /delwarp}, {@code /warps}.
 *
 * <p>Persists warps in SQLite via {@link WarpStore} with case-insensitive lookup by name. Per-warp
 * access is gated on the {@code essentials.warp.use.<name>} permission (or the {@code
 * essentials.warp.use.*} wildcard). Warm-up and cancel-on-move come from the shared {@link
 * DelayedTeleport} service.
 */
public final class WarpsModule extends AbstractModule {

  public WarpsModule() {
    super(new ModuleMetadata("warps", Set.of("teleport"), "0.1.0", "Server warps."));
  }

  @Override
  protected void onEnable() {
    var config = config("warps", WarpsConfig.class, WarpsConfig::defaults);

    var store = new WarpStore(service(SqlExecutor.class));
    var warpService = new WarpService(store);
    registerService(WarpService.class, warpService);

    var delayed = service(DelayedTeleport.class);
    var framework = service(PaperCommandFramework.class);

    registerCommand(new SetWarpCommand(config, warpService));
    registerCommand(new WarpCommand(config, warpService, delayed, framework));
    registerCommand(new DelWarpCommand(config, warpService));
    registerCommand(new WarpsCommand(config, warpService));
  }
}
