package com.hanielcota.essentials.modules.rename;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.rename.command.RenameCommand;
import com.hanielcota.essentials.modules.rename.config.RenameConfig;
import com.hanielcota.essentials.modules.rename.service.RenameService;
import lombok.NonNull;

public final class RenameModule extends AbstractModule {

  public RenameModule() {
    super("rename");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("rename", RenameConfig.class, RenameConfig::defaults);
    registrar.command(new RenameCommand(config, new RenameService()));
  }
}
