package com.hanielcota.essentials.modules.mute;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.database.SqlDialect;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.mute.command.MuteCommand;
import com.hanielcota.essentials.modules.mute.command.MuteNotifier;
import com.hanielcota.essentials.modules.mute.command.UnmuteCommand;
import com.hanielcota.essentials.modules.mute.config.MuteConfig;
import com.hanielcota.essentials.modules.mute.listener.MuteChatListener;
import com.hanielcota.essentials.modules.mute.repository.MuteRepository;
import com.hanielcota.essentials.modules.mute.repository.MuteStore;
import com.hanielcota.essentials.modules.mute.repository.MuteTable;
import com.hanielcota.essentials.modules.mute.service.MuteCache;
import com.hanielcota.essentials.modules.mute.service.MuteService;
import com.hanielcota.essentials.paper.ActorFactory;
import java.time.Instant;
import lombok.NonNull;

public final class MuteModule extends AbstractModule {

  public MuteModule() {
    super("mute");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("mute", MuteConfig.class, MuteConfig::defaults);
    var executor = env.service(SqlExecutor.class);
    var dialect = env.service(SqlDialect.class);
    var table = new MuteTable(dialect);
    table.install(executor);

    var store = new MuteRepository(executor, table);
    var now = Instant.now();
    store.deleteExpired(now);
    var existing = store.listActive(now);

    var writerFactory = env.service(AsyncDatabaseWriter.Factory.class);
    var writer = writerFactory.create("Mutes");
    registrar.closeable(writer);

    var cache = new MuteCache(store, writer);
    cache.loadAll(existing);

    var service = new MuteService(cache);
    registrar.provide(MuteStore.class, store);
    registrar.provide(MuteService.class, service);

    var actors = env.service(ActorFactory.class);
    var notifier = new MuteNotifier(config, actors);

    registrar.command(new MuteCommand(service, notifier));
    registrar.command(new UnmuteCommand(service, notifier));

    registrar.listener(new MuteChatListener(config, service));
  }
}
