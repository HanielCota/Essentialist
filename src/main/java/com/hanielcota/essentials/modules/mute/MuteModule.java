package com.hanielcota.essentials.modules.mute;

import com.hanielcota.essentials.database.DefaultAsyncDatabaseWriter;
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
import com.hanielcota.essentials.modules.mute.repository.MuteStore;
import com.hanielcota.essentials.modules.mute.repository.MuteTable;
import com.hanielcota.essentials.modules.mute.service.MuteService;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
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

    var store = new MuteStore(executor, table);
    var now = Instant.now();
    store.deleteExpired(now);
    var existing = store.listActive(now);

    var writer = new DefaultAsyncDatabaseWriter("Essentialist-Mutes");
    registrar.closeable(writer);

    var service = new MuteService(store, writer);
    service.loadAll(existing);
    registrar.provide(MuteService.class, service);

    var framework = env.service(PaperCommandFramework.class);
    var notifier = new MuteNotifier(config, framework);

    registrar.command(new MuteCommand(service, notifier));
    registrar.command(new UnmuteCommand(service, notifier));

    registrar.listener(new MuteChatListener(config, service));
  }
}
