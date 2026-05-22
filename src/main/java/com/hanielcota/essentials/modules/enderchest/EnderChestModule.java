package com.hanielcota.essentials.modules.enderchest;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.enderchest.command.EnderChestCommand;
import com.hanielcota.essentials.modules.enderchest.config.EnderChestConfig;
import com.hanielcota.essentials.modules.enderchest.service.EnderChestService;

public final class EnderChestModule extends AbstractModule {

  public EnderChestModule() {
    super("enderchest");
  }

  @Override
  protected void onEnable() {
    var config = config("enderchest", EnderChestConfig.class, EnderChestConfig::defaults);
    registerCommand(new EnderChestCommand(config, new EnderChestService()));
  }
}
