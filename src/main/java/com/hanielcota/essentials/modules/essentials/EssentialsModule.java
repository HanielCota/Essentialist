package com.hanielcota.essentials.modules.essentials;

import com.hanielcota.essentials.config.ConfigService;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.essentials.command.EssentialsCommand;
import com.hanielcota.essentials.modules.essentials.config.EssentialsConfig;

public final class EssentialsModule extends AbstractModule {

  public EssentialsModule() {
    super("essentials");
  }

  @Override
  protected void onEnable() {
    var config = config("essentials", EssentialsConfig.class, EssentialsConfig::defaults);
    var configs = service(ConfigService.class);
    var essentialsCommand = new EssentialsCommand(config, configs);

    registerCommand(essentialsCommand);
  }
}
