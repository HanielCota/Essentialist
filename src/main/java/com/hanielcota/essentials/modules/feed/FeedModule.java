package com.hanielcota.essentials.modules.feed;

import com.hanielcota.essentials.command.ActorMessages;
import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.feed.command.FeedCommand;
import com.hanielcota.essentials.modules.feed.config.FeedConfig;
import com.hanielcota.essentials.modules.feed.service.FeedService;

public final class FeedModule extends AbstractModule {

  public FeedModule() {
    super("feed");
  }

  @Override
  protected void onEnable() {
    var config = configure("feed", FeedConfig.class, FeedConfig::defaults, new FeedService());
    registerCommand(
        new FeedCommand(config, service(FeedService.class), service(ActorMessages.class)));
  }
}
