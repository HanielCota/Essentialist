package com.hanielcota.essentials.modules.homes;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.database.async.AsyncDatabaseWriter;
import com.hanielcota.essentials.database.executor.SqlExecutor;
import com.hanielcota.essentials.database.schema.SqlDialect;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleMetadata;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.homes.command.HomeCommand;
import com.hanielcota.essentials.modules.homes.command.HomesCommand;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.config.menu.MaterialNamesConfig;
import com.hanielcota.essentials.modules.homes.create.HomeCreateNotifier;
import com.hanielcota.essentials.modules.homes.create.HomeCreateOrchestrator;
import com.hanielcota.essentials.modules.homes.create.HomeCreateSessions;
import com.hanielcota.essentials.modules.homes.listener.HomeCreateChatListener;
import com.hanielcota.essentials.modules.homes.listener.HomeRenameChatListener;
import com.hanielcota.essentials.modules.homes.listener.HomesCacheListener;
import com.hanielcota.essentials.modules.homes.listener.HomesMenuCleanupListener;
import com.hanielcota.essentials.modules.homes.listener.HomesSessionCleanupListener;
import com.hanielcota.essentials.modules.homes.menu.DeleteHomeClickHandler;
import com.hanielcota.essentials.modules.homes.menu.DeleteHomeDialog;
import com.hanielcota.essentials.modules.homes.menu.HomeClickHandler;
import com.hanielcota.essentials.modules.homes.menu.HomeOptionsClickHandler;
import com.hanielcota.essentials.modules.homes.menu.HomeOptionsMenu;
import com.hanielcota.essentials.modules.homes.menu.HomesActionTarget;
import com.hanielcota.essentials.modules.homes.menu.HomesMenu;
import com.hanielcota.essentials.modules.homes.menu.HomesMenuState;
import com.hanielcota.essentials.modules.homes.menu.MaterialCategoryClickHandler;
import com.hanielcota.essentials.modules.homes.menu.MaterialCategoryMenu;
import com.hanielcota.essentials.modules.homes.menu.MaterialPickerClickHandler;
import com.hanielcota.essentials.modules.homes.menu.MaterialPickerMenu;
import com.hanielcota.essentials.modules.homes.menu.presentation.HomeEntryRenderer;
import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialIconRegistry;
import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialPickerPresentation;
import com.hanielcota.essentials.modules.homes.rename.HomeRenameNotifier;
import com.hanielcota.essentials.modules.homes.rename.HomeRenameOrchestrator;
import com.hanielcota.essentials.modules.homes.rename.HomeRenameSessions;
import com.hanielcota.essentials.modules.homes.rename.HomeRenameTimer;
import com.hanielcota.essentials.modules.homes.repository.CachedHomeRepository;
import com.hanielcota.essentials.modules.homes.repository.HomeCache;
import com.hanielcota.essentials.modules.homes.repository.SqlHomeRepository;
import com.hanielcota.essentials.modules.homes.repository.SqlHomeTable;
import com.hanielcota.essentials.modules.homes.service.HomeLimitReachedMessageResolver;
import com.hanielcota.essentials.modules.homes.service.HomeLimitResolver;
import com.hanielcota.essentials.modules.homes.service.HomeNameResolver;
import com.hanielcota.essentials.modules.homes.service.HomeNameValidator;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.modules.homes.service.HomeTeleporter;
import com.hanielcota.essentials.modules.homes.service.MissingHomeMessageResolver;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.scheduler.Scheduler;
import java.util.Set;
import java.util.function.IntSupplier;
import lombok.NonNull;

/**
 * Per-player homes: {@code /home} (fast teleport) plus the menu-driven {@code /homes} that owns
 * create / teleport / delete / rename / change-icon.
 *
 * <p>/homes opens an interactive menu (paginated) with a "+ Nova home" button (chat prompt for the
 * name) plus the per-home actions: left click teleports (via the shared {@link DelayedTeleport}
 * warm-up), right click opens a delete confirmation, shift+click starts a chat-driven rename, drop
 * (Q) opens a material picker submenu.
 */
public final class HomesModule extends AbstractModule {

  public HomesModule() {
    super(new ModuleMetadata("homes", Set.of("teleport"), "0.1.0", "Per-player homes."));
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("homes", HomesConfig.class, HomesConfig::defaults);
    var materialNames =
        env.config(
            "homes/material-names", MaterialNamesConfig.class, MaterialNamesConfig::defaults);

    var menus = env.service(MenuService.class);
    var actors = env.service(ActorFactory.class);
    var scheduler = env.service(Scheduler.class);
    var delayed = env.service(DelayedTeleport.class);
    var sqlExecutor = env.service(SqlExecutor.class);
    var dialect = env.service(SqlDialect.class);

    // 1. Storage + service layer.
    var homeTable = new SqlHomeTable(dialect);
    homeTable.install(sqlExecutor);
    var sqlRepository = new SqlHomeRepository(sqlExecutor, homeTable);
    var cache = new HomeCache();
    var writerFactory = env.service(AsyncDatabaseWriter.Factory.class);
    var asyncWriter = writerFactory.create("Homes");
    var repository = new CachedHomeRepository(sqlRepository, asyncWriter, cache);
    IntSupplier defaultLimit = () -> config.value().defaultLimit();
    var limits = new HomeLimitResolver(defaultLimit);
    var homeService = new HomeService(repository, limits);

    registrar.closeable(repository);
    registrar.listener(new HomesCacheListener(repository));
    registrar.provide(HomeService.class, homeService);

    // 2. Interaction layer (rename + create flows + teleport + per-player action target).
    var actionTarget = new HomesActionTarget();
    var renameSessions = new HomeRenameSessions();
    var createSessions = new HomeCreateSessions();
    var nameValidator = new HomeNameValidator();
    var nameResolver = new HomeNameResolver(config, nameValidator);
    var teleporter = new HomeTeleporter(config, delayed);
    var renameTimer = new HomeRenameTimer(scheduler);
    var renameNotifier = new HomeRenameNotifier(config);
    var rename =
        new HomeRenameOrchestrator(
            config,
            homeService,
            renameSessions,
            createSessions,
            nameValidator,
            renameTimer,
            renameNotifier);

    var limitReachedResolver = new HomeLimitReachedMessageResolver(config, homeService);
    var createNotifier = new HomeCreateNotifier(config);
    var create =
        new HomeCreateOrchestrator(
            config,
            homeService,
            createSessions,
            renameSessions,
            nameValidator,
            scheduler,
            createNotifier,
            limitReachedResolver);

    registrar.listener(
        new HomesSessionCleanupListener(actionTarget, renameSessions, createSessions));
    registrar.listener(new HomeRenameChatListener(rename, renameSessions));
    registrar.listener(new HomeCreateChatListener(create, createSessions));

    // 3. Menus + dialogs.
    var menuState = new HomesMenuState();
    var renderer = new HomeEntryRenderer(config);
    var clickHandler = new HomeClickHandler(teleporter, actors, actionTarget);
    registrar.menu(new HomesMenu(config, homeService, renderer, clickHandler, menuState, create));

    var optionsClicks =
        new HomeOptionsClickHandler(actionTarget, homeService, teleporter, rename, actors);
    registrar.menu(new HomeOptionsMenu(config, homeService, renderer, actionTarget, optionsClicks));

    var configSnap = config.value();
    var menuConfig = configSnap.menu();
    var materialNamesSnap = materialNames.value();
    var iconRegistry = new MaterialIconRegistry(menuConfig, materialNamesSnap);
    var pickerPresentation = new MaterialPickerPresentation(materialNames);

    var categoryClickHandler = new MaterialCategoryClickHandler(actionTarget);
    var pickerClickHandler =
        new MaterialPickerClickHandler(config, homeService, actionTarget, pickerPresentation);
    var deleteClickHandler = new DeleteHomeClickHandler(config, homeService, actionTarget);

    registrar.menu(new MaterialCategoryMenu(config, categoryClickHandler));
    registrar.menu(new MaterialPickerMenu(config, actionTarget, iconRegistry, pickerClickHandler));
    registrar.menu(new DeleteHomeDialog(config, deleteClickHandler));
    registrar.listener(new HomesMenuCleanupListener(menuState));

    // 4. Commands.
    var missingResolver = new MissingHomeMessageResolver(config, homeService);
    registrar.command(
        new HomeCommand(config, homeService, teleporter, nameResolver, missingResolver));
    registrar.command(new HomesCommand(homeService, menus, menuState));
  }
}
