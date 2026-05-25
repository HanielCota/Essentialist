package com.hanielcota.essentials.modules.feed;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.feed.command.FeedCommand;
import com.hanielcota.essentials.modules.feed.config.FeedConfig;
import com.hanielcota.essentials.modules.feed.service.FeedService;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;

public final class FeedModule extends AbstractModule {

  public FeedModule() {
    super("feed");
  }

  @Override
  protected void onEnable() {
    var feed = new FeedService();
    var config = configure("feed", FeedConfig.class, FeedConfig::defaults, feed);

    var service = service(FeedService.class);
    var players = service(PlayerProvider.class);
    var framework = service(PaperCommandFramework.class);

    var command = new FeedCommand(config, service, players, framework);
    registerCommand(command);
  }
}
