package com.hanielcota.essentials.modules.tpa;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleMetadata;
import com.hanielcota.essentials.modules.tpa.command.TpAcceptCommand;
import com.hanielcota.essentials.modules.tpa.command.TpCancelCommand;
import com.hanielcota.essentials.modules.tpa.command.TpDenyCommand;
import com.hanielcota.essentials.modules.tpa.command.TpaCommand;
import com.hanielcota.essentials.modules.tpa.command.TpaHereCommand;
import com.hanielcota.essentials.modules.tpa.command.TpaHistoryCommand;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.history.AsyncTpaHistory;
import com.hanielcota.essentials.modules.tpa.history.SqliteTpaHistory;
import com.hanielcota.essentials.modules.tpa.history.TpaHistoryTable;
import com.hanielcota.essentials.modules.tpa.listener.TpaHistoryMenuCleanupListener;
import com.hanielcota.essentials.modules.tpa.listener.TpaQuitListener;
import com.hanielcota.essentials.modules.tpa.menu.TpaHistoryEntryRenderer;
import com.hanielcota.essentials.modules.tpa.menu.TpaHistoryMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaHistoryMenuState;
import com.hanielcota.essentials.modules.tpa.notification.TpaNotifier;
import com.hanielcota.essentials.modules.tpa.service.RequestStore;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestExpiry;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.scheduler.Scheduler;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.Set;

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
  protected void onEnable() {
    var config = config("tpa", TpaConfig.class, TpaConfig::defaults);
    var history = history();
    var runtime = requestRuntime(config, history);
    var menuState = registerHistoryMenu(config, history);

    registerCommands(config, history, runtime.requestService(), menuState);

    var quitListener = new TpaQuitListener(runtime.requestService(), runtime.notifier());
    registerListener(quitListener);
  }

  private AsyncTpaHistory history() {
    var executor = service(SqlExecutor.class);
    TpaHistoryTable.install(executor);

    var sqliteBacked = new SqliteTpaHistory(executor);
    var history = new AsyncTpaHistory(sqliteBacked);

    registerCloseable(history);
    return history;
  }

  private TpaRuntime requestRuntime(ConfigHandle<TpaConfig> config, AsyncTpaHistory history) {
    var store = new RequestStore();
    var notifier = new TpaNotifier(config);
    var requestService = new TeleportRequestService(config, store, history, notifier);

    var expiry = new TeleportRequestExpiry(service(Scheduler.class), store, requestService);
    expiry.start();
    registerCloseable(expiry::stop);

    return new TpaRuntime(requestService, notifier);
  }

  private TpaHistoryMenuState registerHistoryMenu(
      ConfigHandle<TpaConfig> config, AsyncTpaHistory history) {
    var menuState = new TpaHistoryMenuState();
    var entryRenderer = new TpaHistoryEntryRenderer(config);
    var menu = new TpaHistoryMenu(config, history, entryRenderer, menuState);

    registerMenu(menu);

    var cleanupListener = new TpaHistoryMenuCleanupListener(menuState);
    registerListener(cleanupListener);

    return menuState;
  }

  private void registerCommands(
      ConfigHandle<TpaConfig> config,
      AsyncTpaHistory history,
      TeleportRequestService requestService,
      TpaHistoryMenuState menuState) {
    var framework = service(PaperCommandFramework.class);

    var tpaCommand = new TpaCommand(config, requestService);
    registerCommand(tpaCommand);

    var tpaHereCommand = new TpaHereCommand(config, requestService);
    registerCommand(tpaHereCommand);

    var tpAcceptCommand = new TpAcceptCommand(config, requestService, framework);
    registerCommand(tpAcceptCommand);

    var tpDenyCommand = new TpDenyCommand(config, requestService, framework);
    registerCommand(tpDenyCommand);

    var tpCancelCommand = new TpCancelCommand(config, requestService);
    registerCommand(tpCancelCommand);

    var menus = service(MenuService.class);
    var playerProvider = service(PlayerProvider.class);
    var tpaHistoryCommand =
        new TpaHistoryCommand(config, history, menus, menuState, playerProvider);
    registerCommand(tpaHistoryCommand);
  }

  private record TpaRuntime(TeleportRequestService requestService, TpaNotifier notifier) {}
}
