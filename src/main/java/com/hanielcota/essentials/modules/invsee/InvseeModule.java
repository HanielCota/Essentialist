package com.hanielcota.essentials.modules.invsee;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.invsee.command.InvseeCommand;
import com.hanielcota.essentials.modules.invsee.config.InvseeConfig;
import com.hanielcota.essentials.modules.invsee.listener.InvseeListener;
import com.hanielcota.essentials.modules.invsee.listener.InvseeProtectionListener;
import com.hanielcota.essentials.modules.invsee.service.InvseeLocks;
import com.hanielcota.essentials.modules.invsee.service.InvseeService;
import com.hanielcota.essentials.modules.invsee.service.InvseeSynchronizer;
import com.hanielcota.essentials.scheduler.Scheduler;
import lombok.NonNull;

public final class InvseeModule extends AbstractModule {

  public InvseeModule() {
    super("invsee");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("invsee", InvseeConfig.class, InvseeConfig::defaults);
    var locks = new InvseeLocks();
    var service = new InvseeService(locks);
    var synchronizer = new InvseeSynchronizer(env.service(Scheduler.class), service);
    registrar.listener(new InvseeListener(synchronizer));
    registrar.listener(new InvseeProtectionListener(service));
    registrar.command(new InvseeCommand(config, service));
  }
}
