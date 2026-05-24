package com.hanielcota.essentials.modules.online;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.online.command.OnlineCommand;
import com.hanielcota.essentials.modules.online.config.OnlineConfig;

public final class OnlineModule extends AbstractModule {

  public OnlineModule() {
    super("online");
  }

  @Override
  protected void onEnable() {
    var config = config("online", OnlineConfig.class, OnlineConfig::defaults);
    registerCommand(new OnlineCommand(config));
  }
}
