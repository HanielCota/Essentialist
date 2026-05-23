package com.hanielcota.essentials.modules.homes;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.database.SqlExecutor;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleMetadata;
import com.hanielcota.essentials.modules.homes.command.DelHomeCommand;
import com.hanielcota.essentials.modules.homes.command.HomeCommand;
import com.hanielcota.essentials.modules.homes.command.HomesCommand;
import com.hanielcota.essentials.modules.homes.command.SetHomeCommand;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.menu.DeleteHomeDialog;
import com.hanielcota.essentials.modules.homes.menu.HomeClickHandler;
import com.hanielcota.essentials.modules.homes.menu.HomeEntryRenderer;
import com.hanielcota.essentials.modules.homes.menu.HomesActionTarget;
import com.hanielcota.essentials.modules.homes.menu.HomesMenu;
import com.hanielcota.essentials.modules.homes.menu.MaterialPickerMenu;
import com.hanielcota.essentials.modules.homes.rename.HomeRenameOrchestrator;
import com.hanielcota.essentials.modules.homes.service.HomeLimitResolver;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.modules.homes.service.HomeStore;
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

    var store = new HomeStore(service(SqlExecutor.class));
    var limits = new HomeLimitResolver(config.value().defaultLimit());
    var homeService = new HomeService(store, limits);
    registerService(HomeService.class, homeService);

    var delayed = service(DelayedTeleport.class);
    var menus = service(MenuService.class);
    var framework = service(PaperCommandFramework.class);
    var scheduler = service(Scheduler.class);

    var actionTarget = new HomesActionTarget();
    registerListener(actionTarget);

    var rename = new HomeRenameOrchestrator(config, homeService, scheduler);
    registerListener(rename);

    var renderer = new HomeEntryRenderer(config);
    var clickHandler =
        new HomeClickHandler(config, homeService, delayed, framework, actionTarget, rename);
    var menu = new HomesMenu(config, homeService, renderer, clickHandler);
    registerMenu(menu);
    registerListener(menu);

    registerMenu(new DeleteHomeDialog(config, homeService, menus, actionTarget));
    registerMenu(new MaterialPickerMenu(config, homeService, menus, actionTarget));

    registerCommand(new SetHomeCommand(config, homeService));
    registerCommand(new HomeCommand(config, homeService, delayed));
    registerCommand(new DelHomeCommand(config, homeService));
    registerCommand(new HomesCommand(config, homeService, menus, menu));
  }
}
