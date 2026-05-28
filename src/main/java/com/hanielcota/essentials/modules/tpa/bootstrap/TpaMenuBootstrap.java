package com.hanielcota.essentials.modules.tpa.bootstrap;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.tpa.command.TpaSendOrchestrator;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.history.AsyncTpaHistory;
import com.hanielcota.essentials.modules.tpa.listener.TpaHistoryMenuCleanupListener;
import com.hanielcota.essentials.modules.tpa.listener.TpaPendingSelectionCleanupListener;
import com.hanielcota.essentials.modules.tpa.listener.TpaPickPlayerFilterCleanupListener;
import com.hanielcota.essentials.modules.tpa.listener.TpaTargetSelectionCleanupListener;
import com.hanielcota.essentials.modules.tpa.menu.TpaBehaviorSettingsMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaBlockedMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaHelpInfoMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaHelpMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaHistoryClickHandler;
import com.hanielcota.essentials.modules.tpa.menu.TpaHistoryMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaHistoryMenuState;
import com.hanielcota.essentials.modules.tpa.menu.TpaHubClickHandler;
import com.hanielcota.essentials.modules.tpa.menu.TpaNotificationSettingsMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaPickPlayerMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaPrivacySettingsMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaProfileMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaSettingsMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaTargetActionClickHandler;
import com.hanielcota.essentials.modules.tpa.menu.TpaTargetActionMenu;
import com.hanielcota.essentials.modules.tpa.menu.favorites.TpaFavoriteActionMenu;
import com.hanielcota.essentials.modules.tpa.menu.favorites.TpaFavoritesMenu;
import com.hanielcota.essentials.modules.tpa.menu.pending.TpaPendingActionMenu;
import com.hanielcota.essentials.modules.tpa.menu.pending.TpaPendingBulkActions;
import com.hanielcota.essentials.modules.tpa.menu.pending.TpaPendingClickHandler;
import com.hanielcota.essentials.modules.tpa.menu.pending.TpaPendingMenu;
import com.hanielcota.essentials.modules.tpa.menu.presentation.TpaHistoryEntryRenderer;
import com.hanielcota.essentials.modules.tpa.menu.presentation.TpaProfileMenuRenderer;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.modules.tpa.service.TpaBlockService;
import com.hanielcota.essentials.modules.tpa.service.TpaContactService;
import com.hanielcota.essentials.modules.tpa.service.TpaPendingSelections;
import com.hanielcota.essentials.modules.tpa.service.TpaPickPlayerCandidates;
import com.hanielcota.essentials.modules.tpa.service.TpaPickPlayerFilters;
import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import com.hanielcota.essentials.modules.tpa.service.TpaTargetSelections;
import com.hanielcota.essentials.modules.tpa.service.favorites.TpaFavoriteService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.PlayerProvider;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class TpaMenuBootstrap {

  private final @NonNull ModuleEnvironment env;
  private final @NonNull ModuleRegistrar registrar;
  private final @NonNull ConfigHandle<TpaConfig> config;

  public TpaHistoryMenuState registerHistoryMenu(@NonNull AsyncTpaHistory history) {
    var menuState = new TpaHistoryMenuState();
    var entryRenderer = new TpaHistoryEntryRenderer(this.config);
    var clickHandler = new TpaHistoryClickHandler(this.config, menuState);
    var menu = new TpaHistoryMenu(this.config, history, entryRenderer, menuState, clickHandler);

    this.registrar.menu(menu);

    var cleanupListener = new TpaHistoryMenuCleanupListener(menuState);
    this.registrar.listener(cleanupListener);

    return menuState;
  }

  public void registerHelpMenu(
      @NonNull TpaProfileService profiles,
      @NonNull TeleportRequestService requests,
      @NonNull TpaFavoriteService favorites,
      @NonNull TpaContactService contacts,
      @NonNull TpaSendOrchestrator dispatcher) {
    var actors = this.env.service(ActorFactory.class);
    var clickHandler = new TpaHubClickHandler(this.config, requests, actors);
    var helpMenu =
        new TpaHelpMenu(this.config, profiles, requests, favorites, contacts, clickHandler);
    this.registrar.menu(helpMenu);

    var profileRenderer = new TpaProfileMenuRenderer(this.config, profiles, requests, contacts);
    var profileMenu = new TpaProfileMenu(this.config, profileRenderer);
    this.registrar.menu(profileMenu);

    var helpInfoMenu = new TpaHelpInfoMenu(this.config);
    this.registrar.menu(helpInfoMenu);
  }

  public void registerPendingMenu(
      @NonNull TeleportRequestService requestService,
      @NonNull TpaBlockService blocks,
      @NonNull TpaRuntimeBootstrap.TpaShared shared,
      @NonNull TpaPendingSelections selections) {
    var bulkActions =
        new TpaPendingBulkActions(
            this.config,
            requestService,
            shared.acceptHandler(),
            shared.replyNotifier(),
            shared.callbacks(),
            shared.actors());
    var clickHandler = new TpaPendingClickHandler(selections, bulkActions);
    var pendingMenu =
        new TpaPendingMenu(this.config, requestService, clickHandler, shared.players());
    this.registrar.menu(pendingMenu);

    var actionMenu =
        new TpaPendingActionMenu(
            this.config,
            requestService,
            blocks,
            selections,
            shared.acceptHandler(),
            shared.replyNotifier(),
            shared.callbacks(),
            shared.actors());
    this.registrar.menu(actionMenu);

    var menus = this.env.service(MenuService.class);
    this.registrar.listener(new TpaPendingSelectionCleanupListener(selections, menus));
  }

  public void registerSettingsMenu(@NonNull TpaProfileService profiles) {
    var settingsMenu = new TpaSettingsMenu(this.config);
    this.registrar.menu(settingsMenu);

    var privacyMenu = new TpaPrivacySettingsMenu(this.config, profiles);
    this.registrar.menu(privacyMenu);

    var notificationMenu = new TpaNotificationSettingsMenu(this.config, profiles);
    this.registrar.menu(notificationMenu);

    var behaviorMenu = new TpaBehaviorSettingsMenu(this.config, profiles);
    this.registrar.menu(behaviorMenu);
  }

  public void registerBlockedMenu(@NonNull TpaBlockService blocks) {
    var menu = new TpaBlockedMenu(this.config, blocks);

    this.registrar.menu(menu);
  }

  public void registerFavoritesMenu(
      @NonNull TpaFavoriteService favorites,
      @NonNull TpaContactService contacts,
      @NonNull TpaProfileService profiles,
      @NonNull TpaRuntimeBootstrap.FavoriteRuntime favoriteRuntime) {
    var players = this.env.service(PlayerProvider.class);
    var menu =
        new TpaFavoritesMenu(
            this.config,
            favorites,
            contacts,
            profiles,
            favoriteRuntime.selections(),
            favoriteRuntime.orchestrator(),
            favoriteRuntime.addNotifier(),
            players);

    this.registrar.menu(menu);
  }

  public void registerFavoriteActionMenu(
      @NonNull TpaFavoriteService favorites,
      @NonNull TpaRuntimeBootstrap.FavoriteRuntime favoriteRuntime,
      @NonNull TpaRuntimeBootstrap.TpaRuntime runtime,
      @NonNull TpaSendOrchestrator dispatcher) {
    var players = this.env.service(PlayerProvider.class);
    var actors = this.env.service(ActorFactory.class);
    var menu =
        new TpaFavoriteActionMenu(
            this.config,
            favorites,
            favoriteRuntime.selections(),
            runtime.requestService(),
            players,
            actors,
            dispatcher);

    this.registrar.menu(menu);
  }

  public void registerTargetActionMenu(
      @NonNull TpaFavoriteService favorites,
      @NonNull TpaTargetSelections selections,
      @NonNull TpaRuntimeBootstrap.FavoriteRuntime favoriteRuntime,
      @NonNull TpaSendOrchestrator dispatcher) {
    var players = this.env.service(PlayerProvider.class);
    var actors = this.env.service(ActorFactory.class);
    var clickHandler =
        new TpaTargetActionClickHandler(
            this.config,
            selections,
            favorites,
            favoriteRuntime.addNotifier(),
            players,
            actors,
            dispatcher);
    var menu = new TpaTargetActionMenu(this.config, selections, favorites, clickHandler);

    this.registrar.menu(menu);

    var menus = this.env.service(MenuService.class);
    this.registrar.listener(new TpaTargetSelectionCleanupListener(selections, menus));
  }

  public void registerPickPlayerMenu(
      @NonNull TpaTargetSelections selections,
      @NonNull TpaFavoriteService favorites,
      @NonNull TpaContactService contacts) {
    var players = this.env.service(PlayerProvider.class);
    var filters = new TpaPickPlayerFilters();
    var candidates = new TpaPickPlayerCandidates(players, favorites, contacts);
    var menu = new TpaPickPlayerMenu(this.config, selections, filters, candidates);

    this.registrar.menu(menu);

    var menus = this.env.service(MenuService.class);
    this.registrar.listener(new TpaPickPlayerFilterCleanupListener(filters, menus));
  }
}
