package com.hanielcota.essentials.modules.warps;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.database.async.AsyncDatabaseWriter;
import com.hanielcota.essentials.database.executor.SqlExecutor;
import com.hanielcota.essentials.database.schema.SqlDialect;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleMetadata;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.modules.warps.command.DelWarpCommand;
import com.hanielcota.essentials.modules.warps.command.SetWarpCommand;
import com.hanielcota.essentials.modules.warps.command.WarpCommand;
import com.hanielcota.essentials.modules.warps.command.WarpPromptFactory;
import com.hanielcota.essentials.modules.warps.command.WarpsCommand;
import com.hanielcota.essentials.modules.warps.config.WarpsConfig;
import com.hanielcota.essentials.modules.warps.listener.WarpOccupancyListener;
import com.hanielcota.essentials.modules.warps.menu.WarpActionClickHandler;
import com.hanielcota.essentials.modules.warps.menu.WarpActionRenderer;
import com.hanielcota.essentials.modules.warps.menu.WarpActionsMenu;
import com.hanielcota.essentials.modules.warps.menu.WarpClickHandler;
import com.hanielcota.essentials.modules.warps.menu.WarpEntryRenderer;
import com.hanielcota.essentials.modules.warps.menu.WarpFilterRenderer;
import com.hanielcota.essentials.modules.warps.menu.WarpOccupantsMenu;
import com.hanielcota.essentials.modules.warps.menu.WarpsMenu;
import com.hanielcota.essentials.modules.warps.repository.SqlWarpFavoriteRepository;
import com.hanielcota.essentials.modules.warps.repository.SqlWarpLikeRepository;
import com.hanielcota.essentials.modules.warps.repository.SqlWarpRepository;
import com.hanielcota.essentials.modules.warps.repository.WarpCache;
import com.hanielcota.essentials.modules.warps.repository.WarpFavoriteTable;
import com.hanielcota.essentials.modules.warps.repository.WarpLikeTable;
import com.hanielcota.essentials.modules.warps.repository.WarpRepository;
import com.hanielcota.essentials.modules.warps.repository.WarpTable;
import com.hanielcota.essentials.modules.warps.service.WarpFavorites;
import com.hanielcota.essentials.modules.warps.service.WarpFilterPreferences;
import com.hanielcota.essentials.modules.warps.service.WarpLikes;
import com.hanielcota.essentials.modules.warps.service.WarpNameValidator;
import com.hanielcota.essentials.modules.warps.service.WarpOccupancy;
import com.hanielcota.essentials.modules.warps.service.WarpResolver;
import com.hanielcota.essentials.modules.warps.service.WarpSelection;
import com.hanielcota.essentials.modules.warps.service.WarpSelectionResolver;
import com.hanielcota.essentials.modules.warps.service.WarpService;
import com.hanielcota.essentials.modules.warps.service.WarpTeleportService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.WorldLookup;
import java.util.Set;
import lombok.NonNull;

/**
 * Server warps. {@code /warp} and {@code /warps} open a paginated menu (the only teleport path);
 * {@code /setwarp} and {@code /delwarp} stay as commands. Each warp has a configurable icon, a live
 * "players here" count, player favorites and likes, and a right-click action submenu.
 */
public final class WarpsModule extends AbstractModule {

  public WarpsModule() {
    super(new ModuleMetadata("warps", Set.of("teleport"), "0.3.0", "Server warps."));
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("warps", WarpsConfig.class, WarpsConfig::defaults);
    var executor = env.service(SqlExecutor.class);
    var dialect = env.service(SqlDialect.class);

    var table = new WarpTable(dialect);
    var favoriteTable = new WarpFavoriteTable(dialect);
    var likeTable = new WarpLikeTable(dialect);
    table.install(executor);
    favoriteTable.install(executor);
    likeTable.install(executor);

    var repository = new SqlWarpRepository(executor, table);
    var cache = new WarpCache();
    cache.loadAll(repository.list());

    var writerFactory = env.service(AsyncDatabaseWriter.Factory.class);
    var writer = writerFactory.create("Warps");
    registrar.closeable(writer);

    var favorites =
        new WarpFavorites(new SqlWarpFavoriteRepository(executor, favoriteTable), writer);
    favorites.loadAll();
    var likes = new WarpLikes(new SqlWarpLikeRepository(executor, likeTable), writer);
    likes.loadAll();

    var warpService = new WarpService(repository, cache, writer, favorites, likes);
    registrar.provide(WarpRepository.class, repository);
    registrar.provide(WarpService.class, warpService);

    var delayed = env.service(DelayedTeleport.class);
    var worldLookup = env.service(WorldLookup.class);
    var actorFactory = env.service(ActorFactory.class);
    var menus = env.service(MenuService.class);

    var warpResolver = new WarpResolver(worldLookup);
    var occupancy = new WarpOccupancy();
    var selection = new WarpSelection();
    var promptFactory = new WarpPromptFactory();
    var teleportService =
        new WarpTeleportService(
            config, warpService, warpResolver, delayed, promptFactory, occupancy, actorFactory);

    var renderer = new WarpEntryRenderer(config, occupancy, likes, favorites);
    var clickHandler = new WarpClickHandler(teleportService, selection);
    var filterRenderer = new WarpFilterRenderer();
    var filterPreferences = new WarpFilterPreferences();
    var selectionResolver = new WarpSelectionResolver(selection, warpService);

    var menu =
        new WarpsMenu(
            config,
            warpService,
            renderer,
            clickHandler,
            filterRenderer,
            occupancy,
            likes,
            favorites,
            filterPreferences);

    var actionRenderer =
        new WarpActionRenderer(config, favorites, likes, occupancy, selectionResolver);
    var actionClicks =
        new WarpActionClickHandler(favorites, likes, teleportService, selectionResolver);
    var actionsMenu = new WarpActionsMenu(actionRenderer, actionClicks);

    var occupantsMenu = new WarpOccupantsMenu(selection, occupancy);

    registrar.menu(menu);
    registrar.menu(actionsMenu);
    registrar.menu(occupantsMenu);

    registrar.listener(new WarpOccupancyListener(occupancy, selection, filterPreferences, config));

    var nameValidator = new WarpNameValidator();
    var setWarpCommand = new SetWarpCommand(config, warpService, nameValidator);
    var warpCommand = new WarpCommand(menus);
    var delWarpCommand = new DelWarpCommand(config, warpService);
    var warpsCommand = new WarpsCommand(menus);

    registrar.command(setWarpCommand);
    registrar.command(warpCommand);
    registrar.command(delWarpCommand);
    registrar.command(warpsCommand);
  }
}
