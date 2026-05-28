package com.hanielcota.essentials.modules.socialspy;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.socialspy.command.SocialSpyCommand;
import com.hanielcota.essentials.modules.socialspy.command.SocialSpyNotifier;
import com.hanielcota.essentials.modules.socialspy.config.SocialSpyConfig;
import com.hanielcota.essentials.modules.socialspy.listener.SocialSpyQuitListener;
import com.hanielcota.essentials.modules.socialspy.service.SocialSpyBroadcaster;
import com.hanielcota.essentials.modules.socialspy.service.SocialSpyService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.PlayerProvider;
import lombok.NonNull;

public final class SocialSpyModule extends AbstractModule {

  public SocialSpyModule() {
    super("socialspy");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var service = new SocialSpyService();
    var config =
        registrar.configure(
            "socialspy",
            SocialSpyConfig.class,
            SocialSpyConfig::defaults,
            SocialSpyService.class,
            service);
    var players = env.service(PlayerProvider.class);
    var actors = env.service(ActorFactory.class);
    var broadcaster = new SocialSpyBroadcaster(config, service, players, actors);

    registrar.provide(SocialSpyBroadcaster.class, broadcaster);

    var notifier = new SocialSpyNotifier(config);
    registrar.command(new SocialSpyCommand(service, notifier));
    registrar.listener(new SocialSpyQuitListener(service));
  }
}
