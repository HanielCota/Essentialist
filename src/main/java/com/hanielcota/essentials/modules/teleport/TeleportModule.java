package com.hanielcota.essentials.modules.teleport;

import com.hanielcota.essentials.database.async.AsyncDatabaseWriter;
import com.hanielcota.essentials.database.executor.SqlExecutor;
import com.hanielcota.essentials.database.schema.SqlDialect;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
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
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import com.hanielcota.essentials.scheduler.Scheduler;
import lombok.NonNull;

public final class TeleportModule extends AbstractModule {

  public TeleportModule() {
    super("teleport");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("teleport", TeleportConfig.class, TeleportConfig::defaults);
    var executor = env.service(SqlExecutor.class);
    var dialect = env.service(SqlDialect.class);
    var historyTable = new TeleportHistoryTable(dialect);
    historyTable.install(executor);

    var writerFactory = env.service(AsyncDatabaseWriter.Factory.class);
    var historyWriter = writerFactory.create("TeleportHistory");
    registrar.closeable(historyWriter);
    var historyDepth = config.value().historyDepth();
    var history = new SqliteTeleportHistory(executor, historyWriter, historyDepth);
    registrar.provide(TeleportHistory.class, history);

    var delayed = new DelayedTeleport(env.service(Scheduler.class));
    registrar.provide(DelayedTeleport.class, delayed);
    registrar.listener(new DelayedTeleportCanceller(delayed));

    var teleportService = new TeleportService();
    registrar.provide(TeleportService.class, teleportService);

    var actors = env.service(ActorFactory.class);
    var notifier = new TeleportNotifier(config, actors);
    var players = env.service(PlayerProvider.class);
    var callbacks = env.service(MainThreadCallbacks.class);
    var dispatcher = new TeleportDispatcher(config, players, notifier, teleportService, callbacks);

    registrar.command(new TeleportCommand(dispatcher));
    registrar.command(new TeleportHereCommand(actors, notifier, teleportService, callbacks));
    registrar.command(new TeleportCancelCommand(config, delayed));
  }
}
