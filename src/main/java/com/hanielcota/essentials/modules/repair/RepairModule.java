package com.hanielcota.essentials.modules.repair;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.repair.command.RepairCommand;
import com.hanielcota.essentials.modules.repair.config.RepairConfig;
import com.hanielcota.essentials.modules.repair.service.RepairService;
import com.hanielcota.essentials.paper.ActorFactory;
import lombok.NonNull;

public final class RepairModule extends AbstractModule {

  public RepairModule() {
    super("repair");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("repair", RepairConfig.class, RepairConfig::defaults);
    var repairService = new RepairService(config);
    var actors = env.service(ActorFactory.class);

    var command = new RepairCommand(config, repairService, actors);

    registrar.command(command);
  }
}
