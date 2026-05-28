package com.hanielcota.essentials.modules.homes;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
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
import com.hanielcota.essentials.modules.homes.command.HomeLimitReachedMessageResolver;
import com.hanielcota.essentials.modules.homes.service.HomeLimitResolver;
import com.hanielcota.essentials.modules.homes.service.HomeNameResolver;
import com.hanielcota.essentials.modules.homes.service.HomeNameValidator;
import com.hanielcota.essentials.modules.homes.service.HomeOrderingPreferences;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.modules.homes.service.HomeTeleporter;
import com.hanielcota.essentials.modules.homes.command.MissingHomeMessageResolver;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.scheduler.Scheduler;
import java.util.Set;
import java.util.function.IntSupplier;
import lombok.NonNull;

/**
 * Per-player homes: {@code /home} (fast teleport) plus the menu-driven {@code /homes} that owns
 * create / teleport / delete / rename / change-icon.
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

    var homeService = wireStorage(env, registrar, config);
    var interaction = wireInteraction(config, homeService, scheduler, delayed, registrar);
    var menuState = new HomesMenuState();
    wireMenus(config, materialNames, homeService, actors, interaction, menuState, registrar);
    wireCommands(config, homeService, menus, interaction, menuState, registrar);
  }

  private HomeService wireStorage(
      @NonNull ModuleEnvironment env,
      @NonNull ModuleRegistrar registrar,
      @NonNull ConfigHandle<HomesConfig> config) {
    var sqlExecutor = env.service(SqlExecutor.class);
    var dialect = env.service(SqlDialect.class);

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

    return homeService;
  }

  private HomesInteraction wireInteraction(
      @NonNull ConfigHandle<HomesConfig> config,
      @NonNull HomeService homeService,
      @NonNull Scheduler scheduler,
      @NonNull DelayedTeleport delayed,
      @NonNull ModuleRegistrar registrar) {
    var actionTarget = new HomesActionTarget();
    var renameSessions = new HomeRenameSessions();
    var createSessions = new HomeCreateSessions();
    var orderingPreferences = new HomeOrderingPreferences();
    var nameValidator = new HomeNameValidator();
    var nameResolver = new HomeNameResolver(nameValidator);
    var teleporter = new HomeTeleporter(config, delayed, homeService);
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
        new HomesSessionCleanupListener(
            actionTarget, renameSessions, createSessions, orderingPreferences));
    registrar.listener(new HomeRenameChatListener(rename, renameSessions));
    registrar.listener(new HomeCreateChatListener(create, createSessions));

    return new HomesInteraction(
        actionTarget,
        renameSessions,
        createSessions,
        orderingPreferences,
        nameResolver,
        teleporter,
        rename,
        create);
  }

  private void wireMenus(
      @NonNull ConfigHandle<HomesConfig> config,
      @NonNull ConfigHandle<MaterialNamesConfig> materialNames,
      @NonNull HomeService homeService,
      @NonNull ActorFactory actors,
      @NonNull HomesInteraction interaction,
      @NonNull HomesMenuState menuState,
      @NonNull ModuleRegistrar registrar) {
    var renderer = new HomeEntryRenderer(config);
    var clickHandler =
        new HomeClickHandler(interaction.teleporter(), actors, interaction.actionTarget());
    var sortRenderer = new com.hanielcota.essentials.modules.homes.menu.presentation.HomesSortRenderer();
    registrar.menu(
        new HomesMenu(
            config,
            homeService,
            renderer,
            clickHandler,
            menuState,
            interaction.create(),
            interaction.orderingPreferences(),
            sortRenderer));

    var optionsClicks =
        new HomeOptionsClickHandler(
            interaction.actionTarget(),
            homeService,
            interaction.teleporter(),
            interaction.rename(),
            actors);
    registrar.menu(
        new HomeOptionsMenu(
            config, homeService, renderer, interaction.actionTarget(), optionsClicks));

    var configSnap = config.value();
    var menuConfig = configSnap.menu();
    var materialNamesSnap = materialNames.value();
    var iconRegistry = new MaterialIconRegistry(menuConfig.picker(), materialNamesSnap);
    var pickerPresentation = new MaterialPickerPresentation(materialNames);

    var categoryClickHandler = new MaterialCategoryClickHandler(interaction.actionTarget());
    var pickerClickHandler =
        new MaterialPickerClickHandler(
            config, homeService, interaction.actionTarget(), pickerPresentation);
    var deleteClickHandler =
        new DeleteHomeClickHandler(config, homeService, interaction.actionTarget());

    registrar.menu(new MaterialCategoryMenu(config, categoryClickHandler));
    registrar.menu(
        new MaterialPickerMenu(
            config, interaction.actionTarget(), iconRegistry, pickerClickHandler));
    registrar.menu(new DeleteHomeDialog(config, deleteClickHandler));
    registrar.listener(new HomesMenuCleanupListener(menuState));
  }

  private void wireCommands(
      @NonNull ConfigHandle<HomesConfig> config,
      @NonNull HomeService homeService,
      @NonNull MenuService menus,
      @NonNull HomesInteraction interaction,
      @NonNull HomesMenuState menuState,
      @NonNull ModuleRegistrar registrar) {
    var missingResolver = new MissingHomeMessageResolver(config, homeService);
    registrar.command(
        new HomeCommand(
            config,
            homeService,
            interaction.teleporter(),
            interaction.nameResolver(),
            missingResolver,
            menus,
            menuState));
    registrar.command(new HomesCommand(homeService, menus, menuState));
  }

  private record HomesInteraction(
      HomesActionTarget actionTarget,
      HomeRenameSessions renameSessions,
      HomeCreateSessions createSessions,
      HomeOrderingPreferences orderingPreferences,
      HomeNameResolver nameResolver,
      HomeTeleporter teleporter,
      HomeRenameOrchestrator rename,
      HomeCreateOrchestrator create) {}
}
