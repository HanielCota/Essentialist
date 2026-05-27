package com.hanielcota.essentials.modules.tpa;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleMetadata;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.tpa.bootstrap.TpaCommandBootstrap;
import com.hanielcota.essentials.modules.tpa.bootstrap.TpaMenuBootstrap;
import com.hanielcota.essentials.modules.tpa.bootstrap.TpaPersistenceBootstrap;
import com.hanielcota.essentials.modules.tpa.bootstrap.TpaRuntimeBootstrap;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.service.TpaPendingSelections;
import com.hanielcota.essentials.modules.tpa.service.TpaTargetSelections;
import java.util.Set;
import lombok.NonNull;

public final class TpaModule extends AbstractModule {

  public TpaModule() {
    super(
        new ModuleMetadata(
            "tpa", Set.of("teleport"), "0.1.0", "Teleport requests with persistent history."));
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("tpa", TpaConfig.class, TpaConfig::defaults);

    var persistence = new TpaPersistenceBootstrap(env, registrar);
    var history = persistence.history();
    var profiles = persistence.profiles();
    var blocks = persistence.blocks();
    var favorites = persistence.favorites();
    var contacts = persistence.contacts();

    var runtimeBootstrap = new TpaRuntimeBootstrap(env, registrar, config);
    var runtime = runtimeBootstrap.requestRuntime(history, profiles, blocks, contacts, favorites);
    var shared = runtimeBootstrap.sharedHelpers(runtime.requestService(), runtime.notifier());
    var favoriteRuntime = runtimeBootstrap.favoriteRuntime(favorites, profiles);
    var dispatcher =
        runtimeBootstrap.sendDispatcher(runtime.requestService(), favorites, profiles, shared);

    var menuBootstrap = new TpaMenuBootstrap(env, registrar, config);
    var historyMenu = menuBootstrap.registerHistoryMenu(history);
    menuBootstrap.registerHelpMenu(
        profiles, runtime.requestService(), runtime.notifier(), favorites, contacts, dispatcher);

    var pendingSelections = new TpaPendingSelections();
    var targetSelections = new TpaTargetSelections();
    menuBootstrap.registerPendingMenu(runtime.requestService(), blocks, shared, pendingSelections);
    menuBootstrap.registerSettingsMenu(profiles);
    menuBootstrap.registerBlockedMenu(blocks);
    menuBootstrap.registerFavoritesMenu(favorites, contacts, profiles, favoriteRuntime);
    menuBootstrap.registerFavoriteActionMenu(favorites, favoriteRuntime, runtime, dispatcher);
    menuBootstrap.registerTargetActionMenu(
        favorites, targetSelections, favoriteRuntime, dispatcher);
    menuBootstrap.registerPickPlayerMenu(targetSelections, favorites, contacts);

    var commandBootstrap = new TpaCommandBootstrap(env, registrar, config);
    commandBootstrap.registerCommands(
        history,
        runtime.requestService(),
        runtime.notifier(),
        blocks,
        historyMenu,
        targetSelections,
        shared);

    runtimeBootstrap.registerQuitListener(runtime, pendingSelections);
  }
}
