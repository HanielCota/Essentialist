package com.hanielcota.essentials.modules.teleport;

import com.hanielcota.essentials.database.DatabaseProvider;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.teleport.config.TeleportMessages;
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
    var config = config("teleport", TeleportMessages.class, TeleportMessages::defaults);
    TeleportHistory history = new SqliteTeleportHistory(service(DatabaseProvider.class));
    registerService(TeleportHistory.class, history);

    TeleportService teleportService = new TeleportService(history);
    registerService(TeleportService.class, teleportService);

    var ctx = new TeleportContext(config, service(PaperCommandFramework.class));
    registerService(TeleportContext.class, ctx);

    registerCommandsInPackage();
  }
}
