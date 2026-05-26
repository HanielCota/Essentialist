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
import com.hanielcota.essentials.modules.tpa.command.TpaBlockCommand;
import com.hanielcota.essentials.modules.tpa.command.TpaCommand;
import com.hanielcota.essentials.modules.tpa.command.TpaFavoriteNotifier;
import com.hanielcota.essentials.modules.tpa.command.TpaFavoritePromptOrchestrator;
import com.hanielcota.essentials.modules.tpa.command.TpaHereCommand;
import com.hanielcota.essentials.modules.tpa.command.TpaHistoryCommand;
import com.hanielcota.essentials.modules.tpa.command.TpaHistoryPresenter;
import com.hanielcota.essentials.modules.tpa.command.TpaNotifier;
import com.hanielcota.essentials.modules.tpa.command.TpaUnblockCommand;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.history.AsyncTpaHistory;
import com.hanielcota.essentials.modules.tpa.history.SqliteTpaHistory;
import com.hanielcota.essentials.modules.tpa.history.TpaHistoryTable;
import com.hanielcota.essentials.modules.tpa.listener.TpaFavoriteChatListener;
import com.hanielcota.essentials.modules.tpa.listener.TpaFavoriteSessionCleanupListener;
import com.hanielcota.essentials.modules.tpa.listener.TpaHistoryMenuCleanupListener;
import com.hanielcota.essentials.modules.tpa.listener.TpaQuitListener;
import com.hanielcota.essentials.modules.tpa.menu.TpaBlockedMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaFavoriteActionMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaFavoritesMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaHelpMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaHistoryEntryRenderer;
import com.hanielcota.essentials.modules.tpa.menu.TpaHistoryMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaHistoryMenuState;
import com.hanielcota.essentials.modules.tpa.menu.TpaPendingClickHandler;
import com.hanielcota.essentials.modules.tpa.menu.TpaPendingMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaSettingsMenu;
import com.hanielcota.essentials.modules.tpa.repository.RequestRepository;
import com.hanielcota.essentials.modules.tpa.repository.TpaBlockRepository;
import com.hanielcota.essentials.modules.tpa.repository.TpaBlockTable;
import com.hanielcota.essentials.modules.tpa.repository.TpaFavoriteRepository;
import com.hanielcota.essentials.modules.tpa.repository.TpaFavoriteTable;
import com.hanielcota.essentials.modules.tpa.repository.TpaProfileRepository;
import com.hanielcota.essentials.modules.tpa.repository.TpaProfileTable;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestExpiry;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaBlockService;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteSelections;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteService;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteSessions;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import com.hanielcota.essentials.scheduler.Scheduler;
import java.util.Set;
import lombok.NonNull;

/**
 * {@code /tpa} request system: {@code /tpa}, {@code /tpahere}, {@code /tpaccept}, {@code /tpdeny},
 * {@code /tpacancel} and a configurable {@code /tpahistory} menu. Depends on the {@code teleport}
 * module for the shared {@link TeleportService}.
 *
 * <p>This class only wires collaborators together; each does one job — see {@link
 * RequestRepository} (state), {@link TeleportRequestService} (orchestration), {@link
 * TeleportRequestExpiry} (timing) and {@link TpaNotifier} (out-of-band messages).
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
    var profiles = profiles(env, registrar);
    var blocks = blocks(env, registrar);
    var favorites = favorites(env, registrar);
    var runtime = requestRuntime(env, registrar, config, history, profiles, blocks);
    var favoriteRuntime = favoriteRuntime(env, registrar, config, favorites);
    var menuState = registerHistoryMenu(registrar, config, history);
    registerHelpMenu(registrar, config, profiles, runtime.requestService(), favorites);
    registerPendingMenu(env, registrar, config, runtime.requestService());
    registerSettingsMenu(registrar, config, profiles);
    registerBlockedMenu(registrar, config, blocks);
    registerFavoritesMenu(registrar, config, favorites, favoriteRuntime);
    registerFavoriteActionMenu(env, registrar, config, favorites, favoriteRuntime, runtime);

    registerCommands(env, registrar, config, history, runtime.requestService(), blocks, menuState);

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

  private TpaProfileService profiles(
      @NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var executor = env.service(SqlExecutor.class);
    var dialect = env.service(SqlDialect.class);
    var table = new TpaProfileTable(dialect);
    table.install(executor);

    var repository = new TpaProfileRepository(executor, table);
    var writer = new DefaultAsyncDatabaseWriter("Essentialist-TpaProfiles");
    registrar.closeable(writer);

    var service = new TpaProfileService(repository, writer);
    service.loadAll(repository.listAll());

    return service;
  }

  private TpaBlockService blocks(
      @NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var executor = env.service(SqlExecutor.class);
    var dialect = env.service(SqlDialect.class);
    var table = new TpaBlockTable(dialect);
    table.install(executor);

    var repository = new TpaBlockRepository(executor, table);
    var writer = new DefaultAsyncDatabaseWriter("Essentialist-TpaBlocks");
    registrar.closeable(writer);

    var service = new TpaBlockService(repository, writer);
    service.loadAll(repository.listAll());

    return service;
  }

  private TpaFavoriteService favorites(
      @NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var executor = env.service(SqlExecutor.class);
    var dialect = env.service(SqlDialect.class);
    var table = new TpaFavoriteTable(dialect);
    table.install(executor);

    var repository = new TpaFavoriteRepository(executor, table);
    var writer = new DefaultAsyncDatabaseWriter("Essentialist-TpaFavorites");
    registrar.closeable(writer);

    var service = new TpaFavoriteService(repository, writer);
    service.loadAll(repository.listAll());

    return service;
  }

  private TpaRuntime requestRuntime(
      @NonNull ModuleEnvironment env,
      @NonNull ModuleRegistrar registrar,
      ConfigHandle<TpaConfig> config,
      AsyncTpaHistory history,
      TpaProfileService profiles,
      TpaBlockService blocks) {
    var store = new RequestRepository();
    var players = env.service(PlayerProvider.class);
    var notifier = new TpaNotifier(config, players);
    var requestService =
        new TeleportRequestService(config, store, history, notifier, players, profiles, blocks);

    var expiry = new TeleportRequestExpiry(env.service(Scheduler.class), store, requestService);
    expiry.start();
    registrar.closeable(expiry::stop);

    return new TpaRuntime(requestService, notifier);
  }

  private void registerHelpMenu(
      @NonNull ModuleRegistrar registrar,
      ConfigHandle<TpaConfig> config,
      TpaProfileService profiles,
      TeleportRequestService requests,
      TpaFavoriteService favorites) {
    var menu = new TpaHelpMenu(config, profiles, requests, favorites);

    registrar.menu(menu);
  }

  private FavoriteRuntime favoriteRuntime(
      @NonNull ModuleEnvironment env,
      @NonNull ModuleRegistrar registrar,
      ConfigHandle<TpaConfig> config,
      TpaFavoriteService favorites) {
    var sessions = new TpaFavoriteSessions();
    var selections = new TpaFavoriteSelections();
    var notifier = new TpaFavoriteNotifier(config);
    var playerProvider = env.service(PlayerProvider.class);
    var scheduler = env.service(Scheduler.class);
    var orchestrator =
        new TpaFavoritePromptOrchestrator(
            config, favorites, sessions, notifier, playerProvider, scheduler);

    var chatListener = new TpaFavoriteChatListener(orchestrator, sessions);
    registrar.listener(chatListener);

    var cleanupListener = new TpaFavoriteSessionCleanupListener(sessions, selections);
    registrar.listener(cleanupListener);

    return new FavoriteRuntime(selections, orchestrator);
  }

  private void registerFavoritesMenu(
      @NonNull ModuleRegistrar registrar,
      ConfigHandle<TpaConfig> config,
      TpaFavoriteService favorites,
      FavoriteRuntime favoriteRuntime) {
    var menu =
        new TpaFavoritesMenu(
            config, favorites, favoriteRuntime.selections(), favoriteRuntime.orchestrator());

    registrar.menu(menu);
  }

  private void registerFavoriteActionMenu(
      @NonNull ModuleEnvironment env,
      @NonNull ModuleRegistrar registrar,
      ConfigHandle<TpaConfig> config,
      TpaFavoriteService favorites,
      FavoriteRuntime favoriteRuntime,
      TpaRuntime runtime) {
    var playerProvider = env.service(PlayerProvider.class);
    var actors = env.service(ActorFactory.class);
    var menu =
        new TpaFavoriteActionMenu(
            config,
            favorites,
            favoriteRuntime.selections(),
            runtime.requestService(),
            playerProvider,
            actors);

    registrar.menu(menu);
  }

  private void registerPendingMenu(
      @NonNull ModuleEnvironment env,
      @NonNull ModuleRegistrar registrar,
      ConfigHandle<TpaConfig> config,
      TeleportRequestService requestService) {
    var actors = env.service(ActorFactory.class);
    var players = env.service(PlayerProvider.class);
    var callbacks = env.service(MainThreadCallbacks.class);
    var acceptHandler = new TpAcceptResultHandler(config, actors, players);
    var clickHandler =
        new TpaPendingClickHandler(
            config, requestService, acceptHandler, callbacks, actors, players);
    var menu = new TpaPendingMenu(config, requestService, clickHandler);

    registrar.menu(menu);
  }

  private void registerSettingsMenu(
      @NonNull ModuleRegistrar registrar,
      ConfigHandle<TpaConfig> config,
      TpaProfileService profiles) {
    var menu = new TpaSettingsMenu(config, profiles);

    registrar.menu(menu);
  }

  private void registerBlockedMenu(
      @NonNull ModuleRegistrar registrar, ConfigHandle<TpaConfig> config, TpaBlockService blocks) {
    var menu = new TpaBlockedMenu(config, blocks);

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
      TpaBlockService blocks,
      TpaHistoryMenuState menuState) {
    var actors = env.service(ActorFactory.class);
    var playerProvider = env.service(PlayerProvider.class);
    var menus = env.service(MenuService.class);

    var tpaCommand = new TpaCommand(config, requestService, playerProvider, menus);
    registrar.command(tpaCommand);

    var tpaHereCommand = new TpaHereCommand(config, requestService);
    registrar.command(tpaHereCommand);

    registrar.command(new TpaBlockCommand(config, blocks, playerProvider));
    registrar.command(new TpaUnblockCommand(config, blocks, playerProvider));

    var acceptResultHandler = new TpAcceptResultHandler(config, actors, playerProvider);
    var callbacks = env.service(MainThreadCallbacks.class);
    var tpAcceptCommand =
        new TpAcceptCommand(config, requestService, acceptResultHandler, callbacks);
    registrar.command(tpAcceptCommand);

    var tpDenyCommand = new TpDenyCommand(config, requestService, actors, playerProvider);
    registrar.command(tpDenyCommand);

    var tpCancelCommand = new TpCancelCommand(config, requestService);
    registrar.command(tpCancelCommand);

    var historyPresenter = new TpaHistoryPresenter(config, history, menus, menuState);
    var tpaHistoryCommand = new TpaHistoryCommand(config, playerProvider, historyPresenter);
    registrar.command(tpaHistoryCommand);
  }

  private record TpaRuntime(TeleportRequestService requestService, TpaNotifier notifier) {}

  private record FavoriteRuntime(
      TpaFavoriteSelections selections, TpaFavoritePromptOrchestrator orchestrator) {}
}
