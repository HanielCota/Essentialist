package com.hanielcota.essentials.modules.homes.factory;

import com.github.hanielcota.menuframework.api.Menu;
import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.listener.HomesMenuCleanupListener;
import com.hanielcota.essentials.modules.homes.menu.DeleteHomeDialog;
import com.hanielcota.essentials.modules.homes.menu.HomeClickHandler;
import com.hanielcota.essentials.modules.homes.menu.HomesActionTarget;
import com.hanielcota.essentials.modules.homes.menu.HomesMenu;
import com.hanielcota.essentials.modules.homes.menu.MaterialPickerMenu;
import com.hanielcota.essentials.modules.homes.menu.presentation.HomeEntryRenderer;
import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialPickerPresentation;
import com.hanielcota.essentials.modules.homes.rename.HomeRenameOrchestrator;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.modules.homes.teleport.HomeTeleporter;
import com.hanielcota.essentials.scheduler.Scheduler;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.List;
import lombok.NonNull;

public final class HomesMenuFactory {

  public HomesMenus create(
      @NonNull ConfigHandle<HomesConfig> config,
      @NonNull HomeService homeService,
      @NonNull MenuService menus,
      @NonNull PaperCommandFramework framework,
      @NonNull HomeTeleporter teleporter,
      @NonNull HomesActionTarget actionTarget,
      @NonNull HomeRenameOrchestrator rename,
      @NonNull Scheduler scheduler) {

    var renderer = new HomeEntryRenderer(config);
    var clickHandler = new HomeClickHandler(teleporter, framework, actionTarget, rename, scheduler);
    var homesMenu = new HomesMenu(config, homeService, renderer, clickHandler);

    var dialogs =
        List.of(
            new DeleteHomeDialog(config, homeService, menus, actionTarget),
            new MaterialPickerMenu(
                config, homeService, menus, actionTarget, new MaterialPickerPresentation()));

    var cleanupListener = new HomesMenuCleanupListener(homesMenu);

    return new HomesMenus(homesMenu, dialogs, cleanupListener);
  }

  public record HomesMenus(
      HomesMenu homesMenu, List<Menu> dialogs, HomesMenuCleanupListener cleanupListener) {}
}
