package com.hanielcota.essentials.modules.actionbar;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.actionbar.command.ActionBarCommand;
import com.hanielcota.essentials.modules.actionbar.config.ActionBarConfig;
import com.hanielcota.essentials.modules.actionbar.service.ActionBarService;
import com.hanielcota.essentials.paper.PlayerProvider;

public final class ActionBarModule extends AbstractModule {

  public ActionBarModule() {
    super("actionbar");
  }

  @Override
  protected void onEnable() {
    var config = config("actionbar", ActionBarConfig.class, ActionBarConfig::defaults);
    var players = service(PlayerProvider.class);
    registerCommand(new ActionBarCommand(config, new ActionBarService(players)));
  }
}
