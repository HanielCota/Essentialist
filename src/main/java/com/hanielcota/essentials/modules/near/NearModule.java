package com.hanielcota.essentials.modules.near;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.near.command.NearCommand;
import com.hanielcota.essentials.modules.near.config.NearConfig;
import com.hanielcota.essentials.modules.near.service.NearService;

public final class NearModule extends AbstractModule {

  public NearModule() {
    super("near");
  }

  @Override
  protected void onEnable() {
    var config = config("near", NearConfig.class, NearConfig::defaults);
    registerCommand(new NearCommand(config, new NearService()));
  }
}
