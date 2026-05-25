package com.hanielcota.essentials.modules.nick;

import com.hanielcota.essentials.database.DefaultAsyncDatabaseWriter;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.nick.command.NickCommand;
import com.hanielcota.essentials.modules.nick.command.NickNotifier;
import com.hanielcota.essentials.modules.nick.command.RealNameCommand;
import com.hanielcota.essentials.modules.nick.config.NickConfig;
import com.hanielcota.essentials.modules.nick.listener.NickJoinListener;
import com.hanielcota.essentials.modules.nick.repository.NickStore;
import com.hanielcota.essentials.modules.nick.repository.NickTable;
import com.hanielcota.essentials.modules.nick.service.NickOperationService;
import com.hanielcota.essentials.modules.nick.service.NickService;
import com.hanielcota.essentials.modules.nick.service.RealNameResolver;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;

public final class NickModule extends AbstractModule {

  public NickModule() {
    super("nick");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("nick", NickConfig.class, NickConfig::defaults);
    var executor = env.service(SqlExecutor.class);
    NickTable.install(executor);

    var store = new NickStore(executor);
    var existing = store.list();

    var writer = new DefaultAsyncDatabaseWriter("Essentialist-Nicks");
    registrar.closeable(writer);

    var service = new NickService(store, writer);
    service.loadAll(existing);
    registrar.provide(NickService.class, service);

    var framework = env.service(PaperCommandFramework.class);
    var players = env.service(PlayerProvider.class);

    var operations = new NickOperationService(config, service);
    var notifier = new NickNotifier(config, framework);

    var realNameResolver = new RealNameResolver(service, players);

    registrar.command(new NickCommand(operations, notifier));
    registrar.command(new RealNameCommand(config, service, realNameResolver));

    registrar.listener(new NickJoinListener(service));
  }
}
