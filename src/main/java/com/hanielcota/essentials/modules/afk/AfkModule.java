package com.hanielcota.essentials.modules.afk;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
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
import lombok.NonNull;

public final class AfkModule extends AbstractModule {

  public AfkModule() {
    super("afk");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("afk", AfkConfig.class, AfkConfig::defaults);
    var audiences = env.service(AudienceProvider.class);
    var players = env.service(PlayerProvider.class);
    var scheduler = env.service(Scheduler.class);

    var service = new AfkService();
    registrar.provide(AfkService.class, service);

    var broadcaster = new AfkBroadcaster(config, audiences);
    var transitions = new AfkTransitions(service, broadcaster);

    var autoChecker = new AfkAutoChecker(config, service, transitions, players, scheduler);
    autoChecker.start();
    registrar.closeable(autoChecker::stop);

    registrar.command(new AfkCommand(service, transitions));

    registrar.listener(new AfkActivityListener(service, transitions));
    registrar.listener(new AfkJoinListener(service));
    registrar.listener(new AfkQuitListener(service));
  }
}
