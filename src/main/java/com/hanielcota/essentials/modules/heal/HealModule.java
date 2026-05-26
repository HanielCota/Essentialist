package com.hanielcota.essentials.modules.heal;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.heal.command.HealCommand;
import com.hanielcota.essentials.modules.heal.config.HealConfig;
import com.hanielcota.essentials.modules.heal.service.HealService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.PlayerProvider;
import lombok.NonNull;

public final class HealModule extends AbstractModule {

  public HealModule() {
    super("heal");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var heal = new HealService();
    var config = registrar.configure("heal", HealConfig.class, HealConfig::defaults, heal);

    var players = env.service(PlayerProvider.class);
    var actors = env.service(ActorFactory.class);

    var command = new HealCommand(config, heal, players, actors);
    registrar.command(command);
  }
}
