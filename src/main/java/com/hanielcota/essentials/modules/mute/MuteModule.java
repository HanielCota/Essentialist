package com.hanielcota.essentials.modules.mute;

import com.hanielcota.essentials.database.DefaultAsyncDatabaseWriter;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.mute.command.MuteCommand;
import com.hanielcota.essentials.modules.mute.command.UnmuteCommand;
import com.hanielcota.essentials.modules.mute.config.MuteConfig;
import com.hanielcota.essentials.modules.mute.listener.MuteChatListener;
import com.hanielcota.essentials.modules.mute.service.MuteService;
import com.hanielcota.essentials.modules.mute.service.MuteStore;
import com.hanielcota.essentials.modules.mute.service.MuteTable;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.time.Instant;

public final class MuteModule extends AbstractModule {

  public MuteModule() {
    super("mute");
  }

  @Override
  protected void onEnable() {
    var config = config("mute", MuteConfig.class, MuteConfig::defaults);
    var executor = service(SqlExecutor.class);
    MuteTable.install(executor);

    var store = new MuteStore(executor);
    var now = Instant.now();
    store.deleteExpired(now);
    var existing = store.listActive(now);

    var writer = new DefaultAsyncDatabaseWriter("Essentialist-Mutes");
    registerCloseable(writer);

    var service = new MuteService(store, writer);
    service.loadAll(existing);
    registerService(MuteService.class, service);

    var framework = service(PaperCommandFramework.class);

    registerCommand(new MuteCommand(config, service, framework));
    registerCommand(new UnmuteCommand(config, service, framework));

    registerListener(new MuteChatListener(config, service));
  }
}
