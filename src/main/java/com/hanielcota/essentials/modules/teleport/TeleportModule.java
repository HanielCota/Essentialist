package com.hanielcota.essentials.modules.teleport;

import com.hanielcota.essentials.database.DefaultAsyncDatabaseWriter;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
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
import lombok.NonNull;

public final class TeleportModule extends AbstractModule {

  public TeleportModule() {
    super("teleport");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("teleport", TeleportConfig.class, TeleportConfig::defaults);
    var executor = env.service(SqlExecutor.class);
    TeleportHistoryTable.install(executor);

    var historyWriter = new DefaultAsyncDatabaseWriter("Essentialist-TeleportHistory");
    registrar.closeable(historyWriter);
    var history = new SqliteTeleportHistory(executor, historyWriter);
    registrar.provide(TeleportHistory.class, history);

    var delayed = new DelayedTeleport(env.service(Scheduler.class));
    registrar.provide(DelayedTeleport.class, delayed);
    registrar.listener(new DelayedTeleportCanceller(delayed));

    var teleportService = new TeleportService();
    registrar.provide(TeleportService.class, teleportService);

    var framework = env.service(PaperCommandFramework.class);
    var notifier = new TeleportNotifier(config, framework);
    var players = env.service(PlayerProvider.class);
    var callbacks = env.service(MainThreadCallbacks.class);
    var dispatcher = new TeleportDispatcher(config, players, notifier, teleportService, callbacks);

    registrar.command(new TeleportCommand(dispatcher));
    registrar.command(new TeleportHereCommand(framework, notifier, teleportService, callbacks));
    registrar.command(new TeleportCancelCommand(config, delayed));
  }
}
