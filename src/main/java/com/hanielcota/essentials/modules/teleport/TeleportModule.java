package com.hanielcota.essentials.modules.teleport;

import com.hanielcota.essentials.database.DefaultAsyncDatabaseWriter;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.teleport.command.TeleportCancelCommand;
import com.hanielcota.essentials.modules.teleport.command.TeleportCommand;
import com.hanielcota.essentials.modules.teleport.command.TeleportDispatcher;
import com.hanielcota.essentials.modules.teleport.command.TeleportHereCommand;
import com.hanielcota.essentials.modules.teleport.command.TeleportNotifier;
import com.hanielcota.essentials.modules.teleport.config.TeleportConfig;
import com.hanielcota.essentials.modules.teleport.history.SqliteTeleportHistory;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistoryTable;
import com.hanielcota.essentials.modules.teleport.listener.DelayedTeleportCanceller;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.modules.teleport.service.TeleportService;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
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

    var historyWriter = new DefaultAsyncDatabaseWriter("Essentialist-TeleportHistory");
    registerCloseable(historyWriter);
    var history = new SqliteTeleportHistory(executor, historyWriter);
    registerService(TeleportHistory.class, history);

    var delayed = new DelayedTeleport(service(Scheduler.class));
    registerService(DelayedTeleport.class, delayed);
    registerListener(new DelayedTeleportCanceller(delayed));

    var teleportService = new TeleportService();
    registerService(TeleportService.class, teleportService);

    var framework = service(PaperCommandFramework.class);
    var notifier = new TeleportNotifier(config, framework);
    var players = service(PlayerProvider.class);
    var callbacks = service(MainThreadCallbacks.class);
    var dispatcher = new TeleportDispatcher(config, players, notifier, teleportService, callbacks);

    registerCommand(new TeleportCommand(dispatcher));
    registerCommand(new TeleportHereCommand(framework, notifier, teleportService, callbacks));
    registerCommand(new TeleportCancelCommand(config, delayed));
  }
}
