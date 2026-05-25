package com.hanielcota.essentials.modules.socialspy;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.socialspy.command.SocialSpyCommand;
import com.hanielcota.essentials.modules.socialspy.config.SocialSpyConfig;
import com.hanielcota.essentials.modules.socialspy.listener.SocialSpyQuitListener;
import com.hanielcota.essentials.modules.socialspy.service.SocialSpyBroadcaster;
import com.hanielcota.essentials.modules.socialspy.service.SocialSpyService;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;

public final class SocialSpyModule extends AbstractModule {

  public SocialSpyModule() {
    super("socialspy");
  }

  @Override
  protected void onEnable() {
    var config =
        configure(
            "socialspy", SocialSpyConfig.class, SocialSpyConfig::defaults, new SocialSpyService());
    var service = service(SocialSpyService.class);
    var players = service(PlayerProvider.class);
    var framework = service(PaperCommandFramework.class);
    var broadcaster = new SocialSpyBroadcaster(config, service, players, framework);

    registerService(SocialSpyBroadcaster.class, broadcaster);

    registerCommand(new SocialSpyCommand(config, service));
    registerListener(new SocialSpyQuitListener(service));
  }
}
