package com.hanielcota.essentials.modules.tpa;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleMetadata;
import com.hanielcota.essentials.modules.teleport.service.TeleportService;
import com.hanielcota.essentials.modules.tpa.command.TpAcceptCommand;
import com.hanielcota.essentials.modules.tpa.command.TpCancelCommand;
import com.hanielcota.essentials.modules.tpa.command.TpDenyCommand;
import com.hanielcota.essentials.modules.tpa.command.TpaCommand;
import com.hanielcota.essentials.modules.tpa.command.TpaHereCommand;
import com.hanielcota.essentials.modules.tpa.command.TpaHistoryCommand;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.history.AsyncTpaHistory;
import com.hanielcota.essentials.modules.tpa.history.SqliteTpaHistory;
import com.hanielcota.essentials.modules.tpa.listener.TpaQuitListener;
import com.hanielcota.essentials.modules.tpa.menu.TpaHistoryEntryRenderer;
import com.hanielcota.essentials.modules.tpa.menu.TpaHistoryMenu;
import com.hanielcota.essentials.modules.tpa.service.RequestStore;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestExpiry;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaNotifier;
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
    var executor = service(SqlExecutor.class);
    SqliteTpaHistory.install(executor);

    var history = new AsyncTpaHistory(new SqliteTpaHistory(executor));

    var store = new RequestStore();
    var notifier = new TpaNotifier(config);
    var requestService =
        new TeleportRequestService(
            config, store, history, service(TeleportService.class), notifier);

    var expiry = new TeleportRequestExpiry(service(Scheduler.class), store, requestService);
    expiry.start();
    registerCloseable(expiry::stop);
    registerCloseable(history);

    var menu = new TpaHistoryMenu(config, history, new TpaHistoryEntryRenderer(config));
    registerMenu(menu);
    registerListener(menu);

    var framework = service(PaperCommandFramework.class);
    var menus = service(MenuService.class);
    registerCommand(new TpaCommand(config, requestService));
    registerCommand(new TpaHereCommand(config, requestService));
    registerCommand(new TpAcceptCommand(config, requestService, framework));
    registerCommand(new TpDenyCommand(config, requestService, framework));
    registerCommand(new TpCancelCommand(config, requestService));
    registerCommand(
        new TpaHistoryCommand(config, history, menus, menu, service(PlayerProvider.class)));

    registerListener(new TpaQuitListener(requestService, notifier));
  }
}
