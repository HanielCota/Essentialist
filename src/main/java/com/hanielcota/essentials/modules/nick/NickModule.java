package com.hanielcota.essentials.modules.nick;

import com.hanielcota.essentials.database.AsyncDatabaseWriter;
import com.hanielcota.essentials.database.SqlDialect;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.nick.command.NickCommand;
import com.hanielcota.essentials.modules.nick.command.NickNotifier;
import com.hanielcota.essentials.modules.nick.command.RealNameCommand;
import com.hanielcota.essentials.modules.nick.config.NickConfig;
import com.hanielcota.essentials.modules.nick.listener.NickJoinListener;
import com.hanielcota.essentials.modules.nick.repository.NickCache;
import com.hanielcota.essentials.modules.nick.repository.NickRepository;
import com.hanielcota.essentials.modules.nick.repository.NickTable;
import com.hanielcota.essentials.modules.nick.repository.SqlNickRepository;
import com.hanielcota.essentials.modules.nick.service.NickOperationService;
import com.hanielcota.essentials.modules.nick.service.NickService;
import com.hanielcota.essentials.modules.nick.service.RealNameResolver;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.PlayerProvider;
import lombok.NonNull;

public final class NickModule extends AbstractModule {

  public NickModule() {
    super("nick");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("nick", NickConfig.class, NickConfig::defaults);
    var executor = env.service(SqlExecutor.class);
    var dialect = env.service(SqlDialect.class);
    var table = new NickTable(dialect);
    table.install(executor);

    var repository = new SqlNickRepository(executor, table);
    var existing = repository.list();

    var writerFactory = env.service(AsyncDatabaseWriter.Factory.class);
    var writer = writerFactory.create("Nicks");
    registrar.closeable(writer);

    var cache = new NickCache(repository, writer);
    cache.loadAll(existing);

    var service = new NickService(cache);
    registrar.provide(NickRepository.class, repository);
    registrar.provide(NickService.class, service);

    var actors = env.service(ActorFactory.class);
    var players = env.service(PlayerProvider.class);

    var operations = new NickOperationService(config, service);
    var notifier = new NickNotifier(config, actors);

    var realNameResolver = new RealNameResolver(service, players);

    registrar.command(new NickCommand(operations, notifier));
    registrar.command(new RealNameCommand(config, service, realNameResolver));

    registrar.listener(new NickJoinListener(service));
  }
}
