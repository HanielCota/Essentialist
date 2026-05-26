package com.hanielcota.essentials.modules.broadcast;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.broadcast.command.BroadcastCommand;
import com.hanielcota.essentials.modules.broadcast.config.BroadcastConfig;
import com.hanielcota.essentials.modules.broadcast.service.BroadcastService;
import com.hanielcota.essentials.paper.AudienceProvider;
import lombok.NonNull;

public final class BroadcastModule extends AbstractModule {

  public BroadcastModule() {
    super("broadcast");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("broadcast", BroadcastConfig.class, BroadcastConfig::defaults);
    var audiences = env.service(AudienceProvider.class);
    var service = new BroadcastService(config, audiences);

    registrar.command(new BroadcastCommand(config, service));
  }
}
