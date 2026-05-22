package com.hanielcota.essentials.modules.kick;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.kick.command.KickCommand;
import com.hanielcota.essentials.modules.kick.config.KickConfig;
import com.hanielcota.essentials.modules.kick.service.KickService;

public final class KickModule extends AbstractModule {

  public KickModule() {
    super("kick");
  }

  @Override
  protected void onEnable() {
    var config = config("kick", KickConfig.class, KickConfig::defaults);
    registerCommand(new KickCommand(config, new KickService()));
  }
}
