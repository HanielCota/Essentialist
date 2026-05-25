package com.hanielcota.essentials.modules.teleport;

import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.teleport.command.TeleportCancelCommand;
import com.hanielcota.essentials.modules.teleport.command.TeleportCommand;
import com.hanielcota.essentials.modules.teleport.command.TeleportHereCommand;
import com.hanielcota.essentials.modules.teleport.command.TeleportNotifier;
import com.hanielcota.essentials.modules.teleport.config.TeleportConfig;
import com.hanielcota.essentials.modules.teleport.history.SqliteTeleportHistory;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistoryTable;
import com.hanielcota.essentials.modules.teleport.listener.DelayedTeleportCanceller;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.scheduler.Scheduler;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;

public final class TeleportModule extends AbstractModule {

  public TeleportModule() {
    super("teleport");
  }

  @Override
  protected void onEnable() {
    var config = config("teleport", TeleportConfig.class, TeleportConfig::defaults);
    var executor = service(SqlExecutor.class);
    TeleportHistoryTable.install(executor);

    var history = new SqliteTeleportHistory(executor);
    registerService(TeleportHistory.class, history);
    registerCloseable(history);

    var delayed = new DelayedTeleport(service(Scheduler.class));
    registerService(DelayedTeleport.class, delayed);
    registerListener(new DelayedTeleportCanceller(delayed));

    var framework = service(PaperCommandFramework.class);
    var notifier = new TeleportNotifier(config, framework);

    registerCommand(new TeleportCommand(framework, notifier));
    registerCommand(new TeleportHereCommand(framework, notifier));
    registerCommand(new TeleportCancelCommand(config, delayed));
  }
}
