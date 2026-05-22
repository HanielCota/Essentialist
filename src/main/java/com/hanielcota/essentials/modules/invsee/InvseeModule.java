package com.hanielcota.essentials.modules.invsee;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.invsee.command.InvseeCommand;
import com.hanielcota.essentials.modules.invsee.config.InvseeConfig;
import com.hanielcota.essentials.modules.invsee.listener.InvseeListener;
import com.hanielcota.essentials.modules.invsee.service.InvseeService;
import com.hanielcota.essentials.modules.invsee.service.InvseeSynchronizer;

public final class InvseeModule extends AbstractModule {

  public InvseeModule() {
    super("invsee");
  }

  @Override
  protected void onEnable() {
    var config = config("invsee", InvseeConfig.class, InvseeConfig::defaults);
    var service = new InvseeService();
    var synchronizer = new InvseeSynchronizer(plugin(), service);
    registerListener(new InvseeListener(synchronizer));
    registerCommand(new InvseeCommand(config, service));
  }
}
