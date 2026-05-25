package com.hanielcota.essentials.modules.broadcast;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.broadcast.command.BroadcastCommand;
import com.hanielcota.essentials.modules.broadcast.config.BroadcastConfig;
import com.hanielcota.essentials.modules.broadcast.service.BroadcastService;
import com.hanielcota.essentials.paper.AudienceProvider;

public final class BroadcastModule extends AbstractModule {

  public BroadcastModule() {
    super("broadcast");
  }

  @Override
  protected void onEnable() {
    var config = config("broadcast", BroadcastConfig.class, BroadcastConfig::defaults);
    var audiences = service(AudienceProvider.class);
    var service = new BroadcastService(config, audiences);

    registerCommand(new BroadcastCommand(config, service));
  }
}
