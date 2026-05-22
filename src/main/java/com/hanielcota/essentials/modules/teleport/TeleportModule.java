package com.hanielcota.essentials.modules.teleport;

import com.hanielcota.essentials.database.DatabaseProvider;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.teleport.command.TeleportCommand;
import com.hanielcota.essentials.modules.teleport.command.TeleportHereCommand;
import com.hanielcota.essentials.modules.teleport.config.TeleportConfig;
import com.hanielcota.essentials.modules.teleport.history.SqliteTeleportHistory;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import com.hanielcota.essentials.modules.teleport.service.TeleportService;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;

public final class TeleportModule extends AbstractModule {

  public TeleportModule() {
    super("teleport");
  }

  @Override
  protected void onEnable() {
    var config = config("teleport", TeleportConfig.class, TeleportConfig::defaults);
    var history = new SqliteTeleportHistory(service(DatabaseProvider.class));
    registerService(TeleportHistory.class, history);
    registerCloseable(history);

    var teleportService = new TeleportService();
    registerService(TeleportService.class, teleportService);

    var framework = service(PaperCommandFramework.class);
    registerCommand(new TeleportCommand(config, teleportService, framework));
    registerCommand(new TeleportHereCommand(config, teleportService, framework));
  }
}
