package com.hanielcota.essentials.modules.actionbar;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.actionbar.command.ActionBarCommand;
import com.hanielcota.essentials.modules.actionbar.config.ActionBarConfig;
import com.hanielcota.essentials.modules.actionbar.service.ActionBarService;
import com.hanielcota.essentials.paper.PlayerProvider;
import lombok.NonNull;

public final class ActionBarModule extends AbstractModule {

  public ActionBarModule() {
    super("actionbar");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("actionbar", ActionBarConfig.class, ActionBarConfig::defaults);
    var players = env.service(PlayerProvider.class);
    registrar.command(new ActionBarCommand(config, new ActionBarService(players)));
  }
}
