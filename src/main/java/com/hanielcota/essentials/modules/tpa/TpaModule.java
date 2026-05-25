package com.hanielcota.essentials.modules.tpa;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.database.DefaultAsyncDatabaseWriter;
import com.hanielcota.essentials.database.SqlDialect;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleMetadata;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.tpa.command.TpAcceptCommand;
import com.hanielcota.essentials.modules.tpa.command.TpAcceptResultHandler;
import com.hanielcota.essentials.modules.tpa.command.TpCancelCommand;
import com.hanielcota.essentials.modules.tpa.command.TpDenyCommand;
import com.hanielcota.essentials.modules.tpa.command.TpaCommand;
import com.hanielcota.essentials.modules.tpa.command.TpaHereCommand;
import com.hanielcota.essentials.modules.tpa.command.TpaHistoryCommand;
import com.hanielcota.essentials.modules.tpa.command.TpaHistoryPresenter;
import com.hanielcota.essentials.modules.tpa.command.TpaNotifier;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.history.AsyncTpaHistory;
import com.hanielcota.essentials.modules.tpa.history.SqliteTpaHistory;
import com.hanielcota.essentials.modules.tpa.history.TpaHistoryTable;
import com.hanielcota.essentials.modules.tpa.listener.TpaHistoryMenuCleanupListener;
import com.hanielcota.essentials.modules.tpa.listener.TpaQuitListener;
import com.hanielcota.essentials.modules.tpa.menu.TpaHelpMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaHistoryEntryRenderer;
import com.hanielcota.essentials.modules.tpa.menu.TpaHistoryMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaHistoryMenuState;
import com.hanielcota.essentials.modules.tpa.service.RequestStore;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestExpiry;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import com.hanielcota.essentials.scheduler.Scheduler;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.Set;
import lombok.NonNull;

/**
 * {@code /tpa} request system: {@code /tpa}, {@code /tpahere}, {@code /tpaccept}, {@code /tpdeny},
 * {@code /tpacancel} and a configurable {@code /tpahistory} menu. Depends on the {@code teleport}
 * module for the shared {@link TeleportService}.
 *
 * <p>This class only wires collaborators together; each does one job — see {@link RequestStore}
 * (state), {@link TeleportRequestService} (orchestration), {@link TeleportRequestExpiry} (timing)
 * and {@link TpaNotifier} (out-of-band messages).
 */
public final class TpaModule extends AbstractModule {

  public TpaModule() {
    super(
        new ModuleMetadata(
            "tpa", Set.of("teleport"), "0.1.0", "Teleport requests with persistent history."));
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("tpa", TpaConfig.class, TpaConfig::defaults);
    var history = history(env, registrar);
    var runtime = requestRuntime(env, registrar, config, history);
    var menuState = registerHistoryMenu(registrar, config, history);
    registerHelpMenu(registrar, config);

    registerCommands(env, registrar, config, history, runtime.requestService(), menuState);

    var quitListener = new TpaQuitListener(runtime.requestService(), runtime.notifier());
    registrar.listener(quitListener);
  }

  private AsyncTpaHistory history(
      @NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var executor = env.service(SqlExecutor.class);
    var dialect = env.service(SqlDialect.class);
    var table = new TpaHistoryTable(dialect);
    table.install(executor);

    var sqliteBacked = new SqliteTpaHistory(executor);
    var writer = new DefaultAsyncDatabaseWriter("Essentialist-TpaHistory");
    registrar.closeable(writer);

    return new AsyncTpaHistory(sqliteBacked, writer);
  }

  private TpaRuntime requestRuntime(
      @NonNull ModuleEnvironment env,
      @NonNull ModuleRegistrar registrar,
      ConfigHandle<TpaConfig> config,
      AsyncTpaHistory history) {
    var store = new RequestStore();
    var players = env.service(PlayerProvider.class);
    var notifier = new TpaNotifier(config, players);
    var requestService = new TeleportRequestService(config, store, history, notifier, players);

    var expiry = new TeleportRequestExpiry(env.service(Scheduler.class), store, requestService);
    expiry.start();
    registrar.closeable(expiry::stop);

    return new TpaRuntime(requestService, notifier);
  }

  private void registerHelpMenu(
      @NonNull ModuleRegistrar registrar, ConfigHandle<TpaConfig> config) {
    var menu = new TpaHelpMenu(config);

    registrar.menu(menu);
  }

  private TpaHistoryMenuState registerHistoryMenu(
      @NonNull ModuleRegistrar registrar, ConfigHandle<TpaConfig> config, AsyncTpaHistory history) {
    var menuState = new TpaHistoryMenuState();
    var entryRenderer = new TpaHistoryEntryRenderer(config);
    var menu = new TpaHistoryMenu(config, history, entryRenderer, menuState);

    registrar.menu(menu);

    var cleanupListener = new TpaHistoryMenuCleanupListener(menuState);
    registrar.listener(cleanupListener);

    return menuState;
  }

  private void registerCommands(
      @NonNull ModuleEnvironment env,
      @NonNull ModuleRegistrar registrar,
      ConfigHandle<TpaConfig> config,
      AsyncTpaHistory history,
      TeleportRequestService requestService,
      TpaHistoryMenuState menuState) {
    var framework = env.service(PaperCommandFramework.class);
    var playerProvider = env.service(PlayerProvider.class);
    var menus = env.service(MenuService.class);

    var tpaCommand = new TpaCommand(config, requestService, playerProvider, menus);
    registrar.command(tpaCommand);

    var tpaHereCommand = new TpaHereCommand(config, requestService);
    registrar.command(tpaHereCommand);

    var acceptResultHandler = new TpAcceptResultHandler(config, framework, playerProvider);
    var callbacks = env.service(MainThreadCallbacks.class);
    var tpAcceptCommand =
        new TpAcceptCommand(config, requestService, acceptResultHandler, callbacks);
    registrar.command(tpAcceptCommand);

    var tpDenyCommand = new TpDenyCommand(config, requestService, framework, playerProvider);
    registrar.command(tpDenyCommand);

    var tpCancelCommand = new TpCancelCommand(config, requestService);
    registrar.command(tpCancelCommand);

    var historyPresenter = new TpaHistoryPresenter(config, history, menus, menuState);
    var tpaHistoryCommand = new TpaHistoryCommand(config, playerProvider, historyPresenter);
    registrar.command(tpaHistoryCommand);
  }

  private record TpaRuntime(TeleportRequestService requestService, TpaNotifier notifier) {}
}
