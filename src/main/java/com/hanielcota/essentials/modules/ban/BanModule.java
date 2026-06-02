package com.hanielcota.essentials.modules.ban;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.database.async.AsyncDatabaseWriter;
import com.hanielcota.essentials.database.executor.SqlExecutor;
import com.hanielcota.essentials.database.schema.SqlDialect;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.ban.command.BanApplyOrchestrator;
import com.hanielcota.essentials.modules.ban.command.BanCommand;
import com.hanielcota.essentials.modules.ban.command.BanListCommand;
import com.hanielcota.essentials.modules.ban.command.BanNotifier;
import com.hanielcota.essentials.modules.ban.config.BanConfig;
import com.hanielcota.essentials.modules.ban.listener.BanLoginListener;
import com.hanielcota.essentials.modules.ban.listener.BanMenuCleanupListener;
import com.hanielcota.essentials.modules.ban.listener.BanNickChatListener;
import com.hanielcota.essentials.modules.ban.menu.BanListClickHandler;
import com.hanielcota.essentials.modules.ban.menu.BanListMenu;
import com.hanielcota.essentials.modules.ban.menu.BanMenuState;
import com.hanielcota.essentials.modules.ban.menu.BanNickOrchestrator;
import com.hanielcota.essentials.modules.ban.menu.BanOptionsClickHandler;
import com.hanielcota.essentials.modules.ban.menu.BanOptionsMenu;
import com.hanielcota.essentials.modules.ban.menu.BanOptionsView;
import com.hanielcota.essentials.modules.ban.menu.BanPickerClickHandler;
import com.hanielcota.essentials.modules.ban.menu.BanPickerMenu;
import com.hanielcota.essentials.modules.ban.repository.BanCache;
import com.hanielcota.essentials.modules.ban.repository.BanRepository;
import com.hanielcota.essentials.modules.ban.repository.BanTable;
import com.hanielcota.essentials.modules.ban.repository.CachedBanRepository;
import com.hanielcota.essentials.modules.ban.repository.SqlBanRepository;
import com.hanielcota.essentials.modules.ban.service.BanNickSessions;
import com.hanielcota.essentials.modules.ban.service.BanService;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.scheduler.Scheduler;
import java.time.Instant;
import lombok.NonNull;

/** Fully menu-driven bans: pick a player (online head or typed name), choose duration + reason. */
public final class BanModule extends AbstractModule {

  public BanModule() {
    super("ban");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("ban", BanConfig.class, BanConfig::defaults);

    var repository = wireStorage(env, registrar);
    var service = new BanService(repository);
    registrar.provide(BanService.class, service);

    var menus = env.service(MenuService.class);
    var scheduler = env.service(Scheduler.class);
    var players = env.service(PlayerProvider.class);

    var notifier = new BanNotifier(config);
    var applyOrchestrator = new BanApplyOrchestrator(config, service, notifier, scheduler);

    var state = new BanMenuState();
    var sessions = new BanNickSessions();

    var pickerClicks = new BanPickerClickHandler(config, state, sessions);
    var optionsClicks = new BanOptionsClickHandler(config, state, applyOrchestrator);
    var optionsView = new BanOptionsView(config, state, optionsClicks);
    var listClicks = new BanListClickHandler(config, service, notifier);
    var nickOrchestrator = new BanNickOrchestrator(config, players, state, menus, scheduler);

    registrar.menu(new BanPickerMenu(config, pickerClicks));
    registrar.menu(new BanOptionsMenu(config, optionsView));
    registrar.menu(new BanListMenu(config, service, listClicks));

    registrar.command(new BanCommand(config, menus));
    registrar.command(new BanListCommand(config, menus));

    registrar.listener(new BanLoginListener(service, notifier));
    registrar.listener(new BanNickChatListener(sessions, nickOrchestrator));
    registrar.listener(new BanMenuCleanupListener(state, sessions));
  }

  private BanRepository wireStorage(
      @NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var executor = env.service(SqlExecutor.class);
    var dialect = env.service(SqlDialect.class);

    var table = new BanTable(dialect);
    table.install(executor);

    var sqlRepository = new SqlBanRepository(executor, table);
    var now = Instant.now();
    sqlRepository.deleteExpired(now);
    var existing = sqlRepository.listActive(now);

    var writerFactory = env.service(AsyncDatabaseWriter.Factory.class);
    var writer = writerFactory.create("Bans");
    registrar.closeable(writer);

    var cache = new BanCache();
    cache.loadAll(existing);

    return new CachedBanRepository(sqlRepository, cache, writer);
  }
}
