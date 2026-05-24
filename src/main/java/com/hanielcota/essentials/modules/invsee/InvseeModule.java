package com.hanielcota.essentials.modules.invsee;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.invsee.command.InvseeCommand;
import com.hanielcota.essentials.modules.invsee.config.InvseeConfig;
import com.hanielcota.essentials.modules.invsee.listener.InvseeListener;
import com.hanielcota.essentials.modules.invsee.listener.InvseeProtectionListener;
import com.hanielcota.essentials.modules.invsee.service.InvseeService;
import com.hanielcota.essentials.modules.invsee.service.InvseeSynchronizer;
import com.hanielcota.essentials.scheduler.Scheduler;

public final class InvseeModule extends AbstractModule {

  public InvseeModule() {
    super("invsee");
  }

  @Override
  protected void onEnable() {
    var config = config("invsee", InvseeConfig.class, InvseeConfig::defaults);
    var service = new InvseeService();
    var synchronizer = new InvseeSynchronizer(service(Scheduler.class), service);
    registerListener(new InvseeListener(synchronizer));
    registerListener(new InvseeProtectionListener(service));
    registerCommand(new InvseeCommand(config, service));
  }
}
