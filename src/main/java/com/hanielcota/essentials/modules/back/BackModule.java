package com.hanielcota.essentials.modules.back;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleMetadata;
import com.hanielcota.essentials.module.ModuleRegistrar;
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
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import java.util.Set;
import lombok.NonNull;

public final class BackModule extends AbstractModule {

  public BackModule() {
    super(new ModuleMetadata("back", Set.of("teleport"), "0.1.0", ""));
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("back", BackConfig.class, BackConfig::defaults);
    var history = env.service(TeleportHistory.class);
    var menus = env.service(MenuService.class);

    var renderer = new BackEntryRenderer(config);
    var callbacks = env.service(MainThreadCallbacks.class);
    var clickHandler = new BackClickHandler(config, history, callbacks);
    var menuState = new BackMenuState();
    var menu = new BackMenu(config, history, renderer, clickHandler, menuState);
    registrar.menu(menu);
    var cleanupListener = new BackMenuCleanupListener(menuState);
    registrar.listener(cleanupListener);

    var backCommand = new BackCommand(config, history, menus, menuState);
    registrar.command(backCommand);

    var deathListener = new PlayerDeathListener(history);
    registrar.listener(deathListener);

    var teleportListener = new PlayerTeleportListener(history);
    registrar.listener(teleportListener);
  }
}
