package com.hanielcota.essentials.modules.kick;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.kick.command.KickCommand;
import com.hanielcota.essentials.modules.kick.config.KickConfig;
import lombok.NonNull;

public final class KickModule extends AbstractModule {

  public KickModule() {
    super("kick");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("kick", KickConfig.class, KickConfig::defaults);
    registrar.command(new KickCommand(config));
  }
}
