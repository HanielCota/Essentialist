package com.hanielcota.essentials.modules.feed;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.feed.command.FeedCommand;
import com.hanielcota.essentials.modules.feed.config.FeedConfig;
import com.hanielcota.essentials.modules.feed.service.FeedService;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;

public final class FeedModule extends AbstractModule {

  public FeedModule() {
    super("feed");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var feed = new FeedService();
    var config = registrar.configure("feed", FeedConfig.class, FeedConfig::defaults, feed);

    var players = env.service(PlayerProvider.class);
    var framework = env.service(PaperCommandFramework.class);

    var command = new FeedCommand(config, feed, players, framework);
    registrar.command(command);
  }
}
