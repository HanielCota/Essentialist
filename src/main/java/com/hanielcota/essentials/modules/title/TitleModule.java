package com.hanielcota.essentials.modules.title;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.title.command.TitleCommand;
import com.hanielcota.essentials.modules.title.config.TitleConfig;
import com.hanielcota.essentials.modules.title.service.TitleService;
import com.hanielcota.essentials.paper.PlayerProvider;

public final class TitleModule extends AbstractModule {

  public TitleModule() {
    super("title");
  }

  @Override
  protected void onEnable() {
    var config = config("title", TitleConfig.class, TitleConfig::defaults);
    var players = service(PlayerProvider.class);
    var service = new TitleService(config, players);
    registerCommand(new TitleCommand(config, service, players));
  }
}
