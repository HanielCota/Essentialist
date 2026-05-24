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
import com.hanielcota.essentials.modules.vanish.service.VanishVisibilityApplier;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;

public final class VanishModule extends AbstractModule {

  public VanishModule() {
    super("vanish");
  }

  @Override
  protected void onEnable() {
    var config = config("vanish", VanishConfig.class, VanishConfig::defaults);
    var service = new VanishService();
    registerService(VanishService.class, service);

    var applier = new VanishVisibilityApplier(plugin());

    registerListener(new VanishJoinListener(service, applier));
    registerListener(new VanishQuitListener(service));
    registerListener(new VanishProtectionListener(service));

    var renderer = new VanishEntryRenderer(config);
    var clickHandler = new VanishClickHandler(config);
    registerMenu(new VanishMenu(config, service, renderer, clickHandler));

    var framework = service(PaperCommandFramework.class);
    var menus = service(MenuService.class);
    registerCommand(new VanishCommand(config, service, applier, framework, menus));
  }
}
