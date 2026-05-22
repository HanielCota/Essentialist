package com.hanielcota.essentials.modules.invsee;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.invsee.command.InvseeCommand;
import com.hanielcota.essentials.modules.invsee.config.InvseeConfig;

public final class InvseeModule extends AbstractModule {

  public InvseeModule() {
    super("invsee");
  }

  @Override
  protected void onEnable() {
    var config = config("invsee", InvseeConfig.class, InvseeConfig::defaults);
    registerCommand(new InvseeCommand(config));
  }
}
