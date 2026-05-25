package com.hanielcota.essentials.modules.essentials;

import com.hanielcota.essentials.config.ConfigService;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.essentials.command.EssentialsCommand;
import com.hanielcota.essentials.modules.essentials.config.EssentialsConfig;
import lombok.NonNull;

public final class EssentialsModule extends AbstractModule {

  public EssentialsModule() {
    super("essentials");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("essentials", EssentialsConfig.class, EssentialsConfig::defaults);
    var configs = env.service(ConfigService.class);
    var essentialsCommand = new EssentialsCommand(config, configs);

    registrar.command(essentialsCommand);
  }
}
