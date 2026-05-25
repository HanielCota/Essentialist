package com.hanielcota.essentials.modules.repair;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.repair.command.RepairCommand;
import com.hanielcota.essentials.modules.repair.config.RepairConfig;
import com.hanielcota.essentials.modules.repair.service.RepairService;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;

public final class RepairModule extends AbstractModule {

  public RepairModule() {
    super("repair");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("repair", RepairConfig.class, RepairConfig::defaults);
    var repairService = new RepairService(config);
    var framework = env.service(PaperCommandFramework.class);

    var command = new RepairCommand(config, repairService, framework);

    registrar.command(command);
  }
}
