package com.hanielcota.essentials.modules.kill;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.kill.command.KillCommand;
import com.hanielcota.essentials.modules.kill.config.KillConfig;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;

public final class KillModule extends AbstractModule {

  public KillModule() {
    super("kill");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("kill", KillConfig.class, KillConfig::defaults);
    var framework = env.service(PaperCommandFramework.class);
    registrar.command(new KillCommand(config, framework));
  }
}
