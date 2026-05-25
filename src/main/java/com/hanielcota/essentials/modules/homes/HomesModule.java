package com.hanielcota.essentials.modules.homes;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.database.DefaultAsyncDatabaseWriter;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleMetadata;
import com.hanielcota.essentials.modules.homes.command.DelHomeCommand;
import com.hanielcota.essentials.modules.homes.command.HomeCommand;
import com.hanielcota.essentials.modules.homes.command.HomesCommand;
import com.hanielcota.essentials.modules.homes.command.SetHomeCommand;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.config.menu.MaterialNamesConfig;
import com.hanielcota.essentials.modules.homes.listener.HomeRenameChatListener;
import com.hanielcota.essentials.modules.homes.listener.HomesCacheListener;
import com.hanielcota.essentials.modules.homes.listener.HomesMenuCleanupListener;
import com.hanielcota.essentials.modules.homes.listener.HomesSessionCleanupListener;
import com.hanielcota.essentials.modules.homes.material.HomeMaterialResolver;
import com.hanielcota.essentials.modules.homes.menu.DeleteHomeClickHandler;
import com.hanielcota.essentials.modules.homes.menu.DeleteHomeDialog;
import com.hanielcota.essentials.modules.homes.menu.HomeClickHandler;
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
import com.hanielcota.essentials.modules.homes.name.HomeNameResolver;
import com.hanielcota.essentials.modules.homes.name.HomeNameValidator;
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
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.modules.homes.service.MissingHomeMessageResolver;
import com.hanielcota.essentials.modules.homes.teleport.HomeTeleporter;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.scheduler.Scheduler;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.Set;
import java.util.function.IntSupplier;

/**
 * Per-player homes: {@code /sethome}, {@code /home}, {@code /delhome}, {@code /homes}.
 *
 * <p>/homes opens an interactive menu (paginated) where each entry is the home's icon. Left click
 * teleports (via the shared {@link DelayedTeleport} warm-up); right click opens a delete
 * confirmation; shift+click starts a chat-driven rename; drop (Q) opens a material picker submenu.
 */
public final class HomesModule extends AbstractModule {

  public HomesModule() {
    super(new ModuleMetadata("homes", Set.of("teleport"), "0.1.0", "Per-player homes."));
  }

  @Override
  protected void onEnable() {
    var config = config("homes", HomesConfig.class, HomesConfig::defaults);
    var materialNames =
        config("homes/material-names", MaterialNamesConfig.class, MaterialNamesConfig::defaults);

    var menus = service(MenuService.class);
    var framework = service(PaperCommandFramework.class);
    var scheduler = service(Scheduler.class);
    var delayed = service(DelayedTeleport.class);
    var sqlExecutor = service(SqlExecutor.class);

    // 1. Storage + service layer.
    SqlHomeTable.install(sqlExecutor);
    var sqlRepository = new SqlHomeRepository(sqlExecutor);
    var cache = new HomeCache();
    var asyncWriter = new DefaultAsyncDatabaseWriter("Essentialist-Homes");
    var repository = new CachedHomeRepository(sqlRepository, asyncWriter, cache);
    IntSupplier defaultLimit = () -> config.value().defaultLimit();
    var limits = new HomeLimitResolver(defaultLimit);
    var homeService = new HomeService(repository, limits);

    registerCloseable(repository);
    registerListener(new HomesCacheListener(repository));
    registerService(HomeService.class, homeService);

    // 2. Interaction layer (rename flow + teleport + per-player action target).
    var actionTarget = new HomesActionTarget();
    var renameSessions = new HomeRenameSessions();
    var nameValidator = new HomeNameValidator();
    var nameResolver = new HomeNameResolver(config, nameValidator);
    var teleporter = new HomeTeleporter(config, delayed);
    var renameTimer = new HomeRenameTimer(scheduler);
    var renameNotifier = new HomeRenameNotifier(config);
    var rename =
        new HomeRenameOrchestrator(
            config, homeService, renameSessions, nameValidator, renameTimer, renameNotifier);

    registerListener(new HomesSessionCleanupListener(actionTarget, renameSessions));
    registerListener(new HomeRenameChatListener(rename, renameSessions));

    // 3. Menus + dialogs.
    var menuState = new HomesMenuState();
    var renderer = new HomeEntryRenderer(config);
    var clickHandler = new HomeClickHandler(teleporter, framework, actionTarget, rename);
    registerMenu(new HomesMenu(config, homeService, renderer, clickHandler, menuState));

    var configSnap = config.value();
    var menuConfig = configSnap.menu();
    var materialNamesSnap = materialNames.value();
    var iconRegistry = new MaterialIconRegistry(menuConfig, materialNamesSnap);
    var pickerPresentation = new MaterialPickerPresentation(materialNames);

    var categoryClickHandler = new MaterialCategoryClickHandler(actionTarget);
    var pickerClickHandler =
        new MaterialPickerClickHandler(config, homeService, actionTarget, pickerPresentation);
    var deleteClickHandler = new DeleteHomeClickHandler(config, homeService, actionTarget);

    registerMenu(new MaterialCategoryMenu(config, categoryClickHandler));
    registerMenu(new MaterialPickerMenu(config, actionTarget, iconRegistry, pickerClickHandler));
    registerMenu(new DeleteHomeDialog(config, deleteClickHandler));
    registerListener(new HomesMenuCleanupListener(menuState));

    // 4. Commands.
    var materialResolver = new HomeMaterialResolver(config);
    var missingResolver = new MissingHomeMessageResolver(config, homeService);
    var limitReachedResolver = new HomeLimitReachedMessageResolver(config, homeService);
    registerCommand(
        new SetHomeCommand(
            config, homeService, nameResolver, materialResolver, limitReachedResolver));
    registerCommand(
        new HomeCommand(config, homeService, teleporter, nameResolver, missingResolver));
    registerCommand(new DelHomeCommand(config, homeService, nameResolver));
    registerCommand(new HomesCommand(config, homeService, menus, menuState));
  }
}
