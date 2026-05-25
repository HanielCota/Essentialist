package com.hanielcota.essentials.modules.seen;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.nick.service.NickService;
import com.hanielcota.essentials.modules.seen.command.SeenCommand;
import com.hanielcota.essentials.modules.seen.config.SeenConfig;
import com.hanielcota.essentials.modules.seen.service.SeenService;
import com.hanielcota.essentials.paper.PlayerProvider;

public final class SeenModule extends AbstractModule {

  public SeenModule() {
    super("seen");
  }

  @Override
  protected void onEnable() {
    var config = config("seen", SeenConfig.class, SeenConfig::defaults);
    var players = service(PlayerProvider.class);
    var registry = context().services();
    var service = new SeenService(() -> registry.find(NickService.class), players);

    registerCommand(new SeenCommand(config, service));
  }
}
