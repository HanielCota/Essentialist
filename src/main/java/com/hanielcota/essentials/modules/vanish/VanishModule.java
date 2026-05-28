package com.hanielcota.essentials.modules.vanish;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.vanish.command.VanishCommand;
import com.hanielcota.essentials.modules.vanish.config.VanishConfig;
import com.hanielcota.essentials.modules.vanish.listener.VanishJoinListener;
import com.hanielcota.essentials.modules.vanish.listener.VanishProtectionListener;
import com.hanielcota.essentials.modules.vanish.listener.VanishQuitListener;
import com.hanielcota.essentials.modules.vanish.menu.VanishClickHandler;
import com.hanielcota.essentials.modules.vanish.menu.VanishEntryRenderer;
import com.hanielcota.essentials.modules.vanish.menu.VanishMenu;
import com.hanielcota.essentials.modules.vanish.service.VanishCleanupService;
import com.hanielcota.essentials.modules.vanish.service.VanishService;
import com.hanielcota.essentials.modules.vanish.service.VanishTransitions;
import com.hanielcota.essentials.modules.vanish.service.VanishVisibilityApplier;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import lombok.NonNull;

public final class VanishModule extends AbstractModule {

  private VanishCleanupService cleanup;

  public VanishModule() {
    super("vanish");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("vanish", VanishConfig.class, VanishConfig::defaults);
    var players = env.service(PlayerProvider.class);

    var service = new VanishService();
    registrar.provide(VanishService.class, service);

    var applier = new VanishVisibilityApplier(env.plugin(), players);
    this.cleanup = new VanishCleanupService(service, applier, players);

    var transitions = new VanishTransitions(service, applier);

    var joinListener = new VanishJoinListener(service, applier);
    var quitListener = new VanishQuitListener(service, transitions);
    var protectionListener = new VanishProtectionListener(service);
    registrar.listener(joinListener);
    registrar.listener(quitListener);
    registrar.listener(protectionListener);

    var renderer = new VanishEntryRenderer(config);
    var callbacks = env.service(MainThreadCallbacks.class);
    var clickHandler = new VanishClickHandler(config, service, players, callbacks);
    var menu = new VanishMenu(config, service, renderer, clickHandler, players);
    registrar.menu(menu);

    var actors = env.service(ActorFactory.class);
    var menus = env.service(MenuService.class);
    var command = new VanishCommand(config, transitions, actors, menus);
    registrar.command(command);
  }

  @Override
  protected void onDisable() {
    if (this.cleanup != null) {
      this.cleanup.revertAll();
      this.cleanup = null;
    }
  }
}
