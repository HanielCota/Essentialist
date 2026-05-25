package com.hanielcota.essentials.modules.afk;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.afk.command.AfkCommand;
import com.hanielcota.essentials.modules.afk.config.AfkConfig;
import com.hanielcota.essentials.modules.afk.listener.AfkActivityListener;
import com.hanielcota.essentials.modules.afk.listener.AfkJoinListener;
import com.hanielcota.essentials.modules.afk.listener.AfkQuitListener;
import com.hanielcota.essentials.modules.afk.service.AfkAutoChecker;
import com.hanielcota.essentials.modules.afk.service.AfkBroadcaster;
import com.hanielcota.essentials.modules.afk.service.AfkService;
import com.hanielcota.essentials.modules.afk.service.AfkTransitions;
import com.hanielcota.essentials.paper.AudienceProvider;
import com.hanielcota.essentials.paper.PlayerProvider;
import com.hanielcota.essentials.scheduler.Scheduler;

public final class AfkModule extends AbstractModule {

  public AfkModule() {
    super("afk");
  }

  @Override
  protected void onEnable() {
    var config = config("afk", AfkConfig.class, AfkConfig::defaults);
    var audiences = service(AudienceProvider.class);
    var players = service(PlayerProvider.class);
    var scheduler = service(Scheduler.class);

    var service = new AfkService();
    registerService(AfkService.class, service);

    var broadcaster = new AfkBroadcaster(config, audiences);
    var transitions = new AfkTransitions(service, broadcaster);

    var autoChecker = new AfkAutoChecker(config, service, transitions, players, scheduler);
    autoChecker.start();
    registerCloseable(autoChecker::stop);

    registerCommand(new AfkCommand(service, transitions));

    registerListener(new AfkActivityListener(service, transitions));
    registerListener(new AfkJoinListener(service));
    registerListener(new AfkQuitListener(service));
  }
}
