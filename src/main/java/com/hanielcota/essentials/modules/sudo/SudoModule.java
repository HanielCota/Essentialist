package com.hanielcota.essentials.modules.sudo;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.sudo.command.SudoCommand;
import com.hanielcota.essentials.modules.sudo.config.SudoConfig;
import com.hanielcota.essentials.modules.sudo.service.SudoService;
import com.hanielcota.essentials.scheduler.Scheduler;
import lombok.NonNull;

public final class SudoModule extends AbstractModule {

  public SudoModule() {
    super("sudo");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("sudo", SudoConfig.class, SudoConfig::defaults);
    var scheduler = env.service(Scheduler.class);

    var service = new SudoService(scheduler);
    var sudoCommand = new SudoCommand(config, service);
    registrar.command(sudoCommand);
  }
}
