package com.hanielcota.essentials.modules.back;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleMetadata;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.back.command.BackCommand;
import com.hanielcota.essentials.modules.back.command.BackOrchestrator;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.back.listener.BackMenuCleanupListener;
import com.hanielcota.essentials.modules.back.listener.PlayerDeathListener;
import com.hanielcota.essentials.modules.back.listener.PlayerTeleportListener;
import com.hanielcota.essentials.modules.back.menu.BackClickHandler;
import com.hanielcota.essentials.modules.back.menu.BackEntryRenderer;
import com.hanielcota.essentials.modules.back.menu.BackMenu;
import com.hanielcota.essentials.modules.back.service.BackEntryProvider;
import com.hanielcota.essentials.modules.back.service.BackPrefetch;
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

    var prefetch = new BackPrefetch();
    var entryProvider = new BackEntryProvider(prefetch, history);

    var renderer = new BackEntryRenderer(config);
    var callbacks = env.service(MainThreadCallbacks.class);
    var clickHandler = new BackClickHandler(config, history, callbacks);
    var menu = new BackMenu(config, renderer, clickHandler, entryProvider);
    registrar.menu(menu);
    var cleanupListener = new BackMenuCleanupListener(prefetch);
    registrar.listener(cleanupListener);

    var orchestrator = new BackOrchestrator(config, history, menus, prefetch);
    var backCommand = new BackCommand(orchestrator);
    registrar.command(backCommand);

    var deathListener = new PlayerDeathListener(history);
    registrar.listener(deathListener);

    var teleportListener = new PlayerTeleportListener(history);
    registrar.listener(teleportListener);
  }
}
