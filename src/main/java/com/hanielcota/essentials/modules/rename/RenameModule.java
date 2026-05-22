package com.hanielcota.essentials.modules.rename;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.rename.command.RenameCommand;
import com.hanielcota.essentials.modules.rename.config.RenameConfig;
import com.hanielcota.essentials.modules.rename.service.RenameService;

public final class RenameModule extends AbstractModule {

  public RenameModule() {
    super("rename");
  }

  @Override
  protected void onEnable() {
    var config = config("rename", RenameConfig.class, RenameConfig::defaults);
    registerCommand(new RenameCommand(config, new RenameService()));
  }
}
