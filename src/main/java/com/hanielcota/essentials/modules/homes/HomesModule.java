package com.hanielcota.essentials.modules.homes;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleMetadata;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.config.menu.MaterialNamesConfig;
import com.hanielcota.essentials.modules.homes.factory.HomesCommandFactory;
import com.hanielcota.essentials.modules.homes.factory.HomesInteractionFactory;
import com.hanielcota.essentials.modules.homes.factory.HomesMenuFactory;
import com.hanielcota.essentials.modules.homes.factory.HomesServiceFactory;
import com.hanielcota.essentials.modules.homes.menu.HomesMenuState;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.scheduler.Scheduler;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.Set;

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
    var runtime = runtimeServices();

    // 1. Core Services Layer
    var sqlExecutor = service(SqlExecutor.class);
    var serviceComponents = new HomesServiceFactory().create(config, sqlExecutor);
    var homeService = serviceComponents.service();

    registerCloseable(serviceComponents.closeable());
    registerListener(serviceComponents.cacheListener());
    registerService(HomeService.class, homeService);

    // 2. Interaction & Flow Layer
    var interactionFactory = new HomesInteractionFactory();
    var interactions =
        interactionFactory.create(
            config, homeService, runtime.scheduler(), runtime.delayed(), runtime.framework());

    registerListener(interactions.teleportListener());
    registerListener(interactions.renameListener());

    // 3. User Interface Layer (Menus & Dialogs)
    var menuState = new HomesMenuState();
    var menuFactory = new HomesMenuFactory();
    var menus =
        menuFactory.create(
            config,
            materialNames,
            homeService,
            runtime.framework(),
            interactions.teleporter(),
            interactions.actionTarget(),
            interactions.rename(),
            menuState);

    registerMenu(menus.homesMenu());
    registerListener(menus.cleanupListener());
    menus.dialogs().forEach(this::registerMenu);

    // 4. Command Dispatch Layer
    var commandFactory = new HomesCommandFactory();
    var commands =
        commandFactory.create(
            config,
            homeService,
            runtime.menus(),
            interactions.teleporter(),
            menuState,
            interactions.nameResolver());

    commands.forEach(this::registerCommand);
  }

  private HomesRuntime runtimeServices() {
    return new HomesRuntime(
        service(MenuService.class),
        service(PaperCommandFramework.class),
        service(Scheduler.class),
        service(DelayedTeleport.class));
  }

  private record HomesRuntime(
      MenuService menus,
      PaperCommandFramework framework,
      Scheduler scheduler,
      DelayedTeleport delayed) {}
}
