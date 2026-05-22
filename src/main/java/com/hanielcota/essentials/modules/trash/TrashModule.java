package com.hanielcota.essentials.modules.trash;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.trash.command.TrashCommand;
import com.hanielcota.essentials.modules.trash.config.TrashConfig;
import com.hanielcota.essentials.modules.trash.service.TrashService;

public final class TrashModule extends AbstractModule {

  public TrashModule() {
    super("trash");
  }

  @Override
  protected void onEnable() {
    var config = config("trash", TrashConfig.class, TrashConfig::defaults);
    registerCommand(new TrashCommand(config, new TrashService()));
  }
}
