package com.hanielcota.essentials.modules.homes.factory;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.EssentialsMenu;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.listener.HomesMenuCleanupListener;
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
import com.hanielcota.essentials.modules.homes.rename.HomeRenameOrchestrator;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.modules.homes.teleport.HomeTeleporter;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.util.List;
import lombok.NonNull;

public final class HomesMenuFactory {

  public HomesMenus create(
      @NonNull ConfigHandle<HomesConfig> config,
      @NonNull HomeService homeService,
      @NonNull PaperCommandFramework framework,
      @NonNull HomeTeleporter teleporter,
      @NonNull HomesActionTarget actionTarget,
      @NonNull HomeRenameOrchestrator rename,
      @NonNull HomesMenuState menuState) {

    var renderer = new HomeEntryRenderer(config);
    var clickHandler = new HomeClickHandler(teleporter, framework, actionTarget, rename);
    var homesMenu = new HomesMenu(config, homeService, renderer, clickHandler, menuState);

    var iconRegistry = new MaterialIconRegistry(config.value().menu());
    var pickerPresentation = new MaterialPickerPresentation();

    var categoryClickHandler = new MaterialCategoryClickHandler(actionTarget);
    var pickerClickHandler =
        new MaterialPickerClickHandler(config, homeService, actionTarget, pickerPresentation);
    var deleteClickHandler = new DeleteHomeClickHandler(config, homeService, actionTarget);

    var categoryMenu = new MaterialCategoryMenu(config, categoryClickHandler);
    var pickerMenu = new MaterialPickerMenu(config, actionTarget, iconRegistry, pickerClickHandler);

    var dialogs =
        List.of(categoryMenu, new DeleteHomeDialog(config, deleteClickHandler), pickerMenu);

    var cleanupListener = new HomesMenuCleanupListener(menuState);

    return new HomesMenus(homesMenu, dialogs, cleanupListener);
  }

  public record HomesMenus(
      HomesMenu homesMenu,
      List<EssentialsMenu> dialogs,
      HomesMenuCleanupListener cleanupListener) {}
}
