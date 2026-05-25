package com.hanielcota.essentials.modules.vanish;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.vanish.command.VanishCommand;
import com.hanielcota.essentials.modules.vanish.config.VanishConfig;
import com.hanielcota.essentials.modules.vanish.listener.VanishJoinListener;
import com.hanielcota.essentials.modules.vanish.listener.VanishProtectionListener;
import com.hanielcota.essentials.modules.vanish.listener.VanishQuitListener;
import com.hanielcota.essentials.modules.vanish.menu.VanishClickHandler;
import com.hanielcota.essentials.modules.vanish.menu.VanishEntryRenderer;
import com.hanielcota.essentials.modules.vanish.menu.VanishMenu;
import com.hanielcota.essentials.modules.vanish.service.VanishService;
import com.hanielcota.essentials.modules.vanish.service.VanishTransitions;
import com.hanielcota.essentials.modules.vanish.service.VanishVisibilityApplier;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import lombok.NonNull;

public final class VanishModule extends AbstractModule {

  private VanishService service;
  private VanishVisibilityApplier applier;
  private PlayerProvider players;

  public VanishModule() {
    super("vanish");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("vanish", VanishConfig.class, VanishConfig::defaults);
    this.players = env.service(PlayerProvider.class);

    this.service = new VanishService();
    registrar.provide(VanishService.class, this.service);

    this.applier = new VanishVisibilityApplier(env.plugin(), this.players);
    var transitions = new VanishTransitions(this.service, this.applier);

    var joinListener = new VanishJoinListener(this.service, this.applier);
    var quitListener = new VanishQuitListener(this.service, transitions);
    var protectionListener = new VanishProtectionListener(this.service);
    registrar.listener(joinListener);
    registrar.listener(quitListener);
    registrar.listener(protectionListener);

    var renderer = new VanishEntryRenderer(config);
    var callbacks = env.service(MainThreadCallbacks.class);
    var clickHandler = new VanishClickHandler(config, this.service, this.players, callbacks);
    var menu = new VanishMenu(config, this.service, renderer, clickHandler, this.players);
    registrar.menu(menu);

    var actors = env.service(ActorFactory.class);
    var menus = env.service(MenuService.class);
    var command = new VanishCommand(config, transitions, actors, menus);
    registrar.command(command);
  }

  @Override
  protected void onDisable() {
    if (this.service == null || this.applier == null) {
      return;
    }

    // setInvulnerable / setCanPickupItems persist to player NBT — every still-vanished player
    // must be unapplied or they rejoin permanently invulnerable.
    var vanishedIds = this.service.vanished();
    for (var id : vanishedIds) {
      var player = this.players.online(id).orElse(null);
      if (player == null) {
        continue;
      }
      this.applier.unapply(player);
    }

    this.service = null;
    this.applier = null;
    this.players = null;
  }
}
