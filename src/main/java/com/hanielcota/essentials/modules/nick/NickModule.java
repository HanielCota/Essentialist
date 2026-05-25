package com.hanielcota.essentials.modules.nick;

import com.hanielcota.essentials.database.DefaultAsyncDatabaseWriter;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.nick.command.NickCommand;
import com.hanielcota.essentials.modules.nick.command.NickNotifier;
import com.hanielcota.essentials.modules.nick.command.RealNameCommand;
import com.hanielcota.essentials.modules.nick.config.NickConfig;
import com.hanielcota.essentials.modules.nick.listener.NickJoinListener;
import com.hanielcota.essentials.modules.nick.service.NickOperationService;
import com.hanielcota.essentials.modules.nick.service.NickService;
import com.hanielcota.essentials.modules.nick.service.NickStore;
import com.hanielcota.essentials.modules.nick.service.NickTable;
import com.hanielcota.essentials.modules.nick.service.RealNameResolver;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;

public final class NickModule extends AbstractModule {

  public NickModule() {
    super("nick");
  }

  @Override
  protected void onEnable() {
    var config = config("nick", NickConfig.class, NickConfig::defaults);
    var executor = service(SqlExecutor.class);
    NickTable.install(executor);

    var store = new NickStore(executor);
    var existing = store.list();

    var writer = new DefaultAsyncDatabaseWriter("Essentialist-Nicks");
    registerCloseable(writer);

    var service = new NickService(store, writer);
    service.loadAll(existing);
    registerService(NickService.class, service);

    var framework = service(PaperCommandFramework.class);
    var players = service(PlayerProvider.class);

    var operations = new NickOperationService(config, service);
    var notifier = new NickNotifier(config, framework);

    var realNameResolver = new RealNameResolver(service, players);

    registerCommand(new NickCommand(operations, notifier));
    registerCommand(new RealNameCommand(config, service, realNameResolver));

    registerListener(new NickJoinListener(service));
  }
}
