package com.hanielcota.essentials.modules.god;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.god.command.GodCommand;
import com.hanielcota.essentials.modules.god.command.GodNotifier;
import com.hanielcota.essentials.modules.god.config.GodConfig;
import com.hanielcota.essentials.modules.god.listener.GodDamageListener;
import com.hanielcota.essentials.modules.god.listener.GodQuitListener;
import com.hanielcota.essentials.modules.god.service.GodService;
import com.hanielcota.essentials.paper.ActorFactory;
import lombok.NonNull;

public final class GodModule extends AbstractModule {

  public GodModule() {
    super("god");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var god = new GodService();
    var config =
        registrar.configure("god", GodConfig.class, GodConfig::defaults, GodService.class, god);
    var actors = env.service(ActorFactory.class);

    var notifier = new GodNotifier(config, actors);
    var godCommand = new GodCommand(god, notifier);
    registrar.command(godCommand);

    var damageListener = new GodDamageListener(god);
    registrar.listener(damageListener);

    var quitListener = new GodQuitListener(god);
    registrar.listener(quitListener);
  }
}
