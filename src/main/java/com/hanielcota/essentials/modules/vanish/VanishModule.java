package com.hanielcota.essentials.modules.vanish;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.module.AbstractModule;
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
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import org.bukkit.Bukkit;

public final class VanishModule extends AbstractModule {

  private VanishService service;
  private VanishVisibilityApplier applier;

  public VanishModule() {
    super("vanish");
  }

  @Override
  protected void onEnable() {
    var config = config("vanish", VanishConfig.class, VanishConfig::defaults);

    this.service = new VanishService();
    registerService(VanishService.class, this.service);

    this.applier = new VanishVisibilityApplier(plugin());
    var transitions = new VanishTransitions(this.service, this.applier);

    var joinListener = new VanishJoinListener(this.service, this.applier);
    var quitListener = new VanishQuitListener(this.service, transitions);
    var protectionListener = new VanishProtectionListener(this.service);
    registerListener(joinListener);
    registerListener(quitListener);
    registerListener(protectionListener);

    var renderer = new VanishEntryRenderer(config);
    var clickHandler = new VanishClickHandler(config, this.service);
    var menu = new VanishMenu(config, this.service, renderer, clickHandler);
    registerMenu(menu);

    var framework = service(PaperCommandFramework.class);
    var menus = service(MenuService.class);
    var command = new VanishCommand(config, transitions, framework, menus);
    registerCommand(command);
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
      var player = Bukkit.getPlayer(id);
      if (player == null) {
        continue;
      }
      this.applier.unapply(player);
    }

    this.service = null;
    this.applier = null;
  }
}
