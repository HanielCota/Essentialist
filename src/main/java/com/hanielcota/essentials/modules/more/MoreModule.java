package com.hanielcota.essentials.modules.more;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.more.command.MoreCommand;
import com.hanielcota.essentials.modules.more.config.MoreConfig;
import com.hanielcota.essentials.modules.more.service.MoreService;
import lombok.NonNull;

public final class MoreModule extends AbstractModule {

  public MoreModule() {
    super("more");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("more", MoreConfig.class, MoreConfig::defaults);
    var service = new MoreService();

    var moreCommand = new MoreCommand(config, service);
    registrar.command(moreCommand);
  }
}
