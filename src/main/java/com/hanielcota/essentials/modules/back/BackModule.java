package com.hanielcota.essentials.modules.back;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleMetadata;
import com.hanielcota.essentials.modules.back.command.BackCommand;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.back.listener.BackMenuCleanupListener;
import com.hanielcota.essentials.modules.back.listener.PlayerDeathListener;
import com.hanielcota.essentials.modules.back.listener.PlayerTeleportListener;
import com.hanielcota.essentials.modules.back.menu.BackClickHandler;
import com.hanielcota.essentials.modules.back.menu.BackEntryRenderer;
import com.hanielcota.essentials.modules.back.menu.BackMenu;
import com.hanielcota.essentials.modules.back.menu.BackMenuState;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import java.util.Set;

public final class BackModule extends AbstractModule {

  public BackModule() {
    super(new ModuleMetadata("back", Set.of("teleport"), "0.1.0", ""));
  }

  @Override
  protected void onEnable() {
    var config = config("back", BackConfig.class, BackConfig::defaults);
    var history = service(TeleportHistory.class);
    var menus = service(MenuService.class);

    var renderer = new BackEntryRenderer(config);
    var clickHandler = new BackClickHandler(config, history);
    var menuState = new BackMenuState();
    var menu = new BackMenu(config, history, renderer, clickHandler, menuState);
    registerMenu(menu);
    var cleanupListener = new BackMenuCleanupListener(menuState);
    registerListener(cleanupListener);

    var backCommand = new BackCommand(config, history, menus, menuState);
    registerCommand(backCommand);

    var deathListener = new PlayerDeathListener(history);
    registerListener(deathListener);

    var teleportListener = new PlayerTeleportListener(history);
    registerListener(teleportListener);
  }
}
