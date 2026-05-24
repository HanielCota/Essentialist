package com.hanielcota.essentials.modules.teleport;

import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.teleport.command.TeleportCommand;
import com.hanielcota.essentials.modules.teleport.command.TeleportHereCommand;
import com.hanielcota.essentials.modules.teleport.config.TeleportConfig;
import com.hanielcota.essentials.modules.teleport.history.SqliteTeleportHistory;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.modules.teleport.service.TeleportService;
import com.hanielcota.essentials.scheduler.Scheduler;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;

public final class TeleportModule extends AbstractModule {

  public TeleportModule() {
    super("teleport");
  }

  @Override
  protected void onEnable() {
    var config = config("teleport", TeleportConfig.class, TeleportConfig::defaults);
    var history = new SqliteTeleportHistory(service(SqlExecutor.class));
    registerService(TeleportHistory.class, history);
    registerCloseable(history);

    var teleportService = new TeleportService();
    registerService(TeleportService.class, teleportService);

    var delayed = new DelayedTeleport(service(Scheduler.class), teleportService);
    registerService(DelayedTeleport.class, delayed);
    registerListener(delayed);

    var framework = service(PaperCommandFramework.class);
    var teleportCommand = new TeleportCommand(config, teleportService, framework);
    registerCommand(teleportCommand);

    var teleportHereCommand = new TeleportHereCommand(config, teleportService, framework);
    registerCommand(teleportHereCommand);
  }
}
