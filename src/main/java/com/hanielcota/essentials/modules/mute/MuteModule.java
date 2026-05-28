package com.hanielcota.essentials.modules.mute;

import com.hanielcota.essentials.database.async.AsyncDatabaseWriter;
import com.hanielcota.essentials.database.executor.SqlExecutor;
import com.hanielcota.essentials.database.schema.SqlDialect;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.mute.command.MuteCommand;
import com.hanielcota.essentials.modules.mute.command.MuteNotifier;
import com.hanielcota.essentials.modules.mute.command.UnmuteCommand;
import com.hanielcota.essentials.modules.mute.config.MuteConfig;
import com.hanielcota.essentials.modules.mute.listener.MuteChatListener;
import com.hanielcota.essentials.modules.mute.listener.MuteCommandListener;
import com.hanielcota.essentials.modules.mute.repository.CachedMuteRepository;
import com.hanielcota.essentials.modules.mute.repository.MuteCache;
import com.hanielcota.essentials.modules.mute.repository.MuteRepository;
import com.hanielcota.essentials.modules.mute.repository.MuteTable;
import com.hanielcota.essentials.modules.mute.repository.SqlMuteRepository;
import com.hanielcota.essentials.modules.mute.command.MuteBlockMessageRenderer;
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

    var sqlRepository = new SqlMuteRepository(executor, table);
    var now = Instant.now();
    sqlRepository.deleteExpired(now);
    var existing = sqlRepository.listActive(now);

    var writerFactory = env.service(AsyncDatabaseWriter.Factory.class);
    var writer = writerFactory.create("Mutes");
    registrar.closeable(writer);

    var cache = new MuteCache();
    cache.loadAll(existing);

    var repository = new CachedMuteRepository(sqlRepository, cache, writer);
    var service = new MuteService(repository);

    registrar.provide(MuteRepository.class, repository);
    registrar.provide(MuteService.class, service);

    var actors = env.service(ActorFactory.class);
    var notifier = new MuteNotifier(config, actors);

    registrar.command(new MuteCommand(service, notifier));
    registrar.command(new UnmuteCommand(service, notifier));

    var blockRenderer = new MuteBlockMessageRenderer(config);

    registrar.listener(new MuteChatListener(service, blockRenderer));
    registrar.listener(new MuteCommandListener(config, service, blockRenderer));
  }
}
