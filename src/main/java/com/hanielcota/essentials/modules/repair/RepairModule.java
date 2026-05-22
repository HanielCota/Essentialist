package com.hanielcota.essentials.modules.repair;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.repair.command.RepairCommand;
import com.hanielcota.essentials.modules.repair.config.RepairConfig;
import com.hanielcota.essentials.modules.repair.service.RepairService;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;

public final class RepairModule extends AbstractModule {

  public RepairModule() {
    super("repair");
  }

  @Override
  protected void onEnable() {
    var config = config("repair", RepairConfig.class, RepairConfig::defaults);
    var repairService = new RepairService(config);
    registerService(RepairService.class, repairService);

    var framework = service(PaperCommandFramework.class);
    registerCommand(new RepairCommand(config, repairService, framework));
  }
}
