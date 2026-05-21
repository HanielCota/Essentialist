package com.hanielcota.essentials.modules.back;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleMetadata;
import com.hanielcota.essentials.modules.back.command.BackCommand;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.back.listener.PlayerDeathListener;
import com.hanielcota.essentials.modules.back.menu.BackClickHandler;
import com.hanielcota.essentials.modules.back.menu.BackEntryRenderer;
import com.hanielcota.essentials.modules.back.menu.BackMenu;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import com.hanielcota.essentials.modules.teleport.service.TeleportService;
import java.util.Set;

public final class BackModule extends AbstractModule {

  public BackModule() {
    super(new ModuleMetadata("back", Set.of("teleport"), "0.1.0", ""));
  }

  @Override
  protected void onEnable() {
    var config = config("back", BackConfig.class, BackConfig::defaults);
    var history = service(TeleportHistory.class);
    var teleport = service(TeleportService.class);
    var menus = service(MenuService.class);

    var renderer = new BackEntryRenderer(config);
    var clickHandler = new BackClickHandler(config, history, teleport);
    registerMenu(new BackMenu(config, history, renderer, clickHandler));
    registerCommand(new BackCommand(config, history, menus));
    registerListener(new PlayerDeathListener(history));
  }
}
