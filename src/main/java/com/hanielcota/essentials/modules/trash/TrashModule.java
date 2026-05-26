package com.hanielcota.essentials.modules.trash;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.trash.command.TrashCommand;
import com.hanielcota.essentials.modules.trash.config.TrashConfig;
import lombok.NonNull;

public final class TrashModule extends AbstractModule {

  public TrashModule() {
    super("trash");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("trash", TrashConfig.class, TrashConfig::defaults);
    registrar.command(new TrashCommand(config));
  }
}
