package com.hanielcota.essentials.modules.clearchat;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.clearchat.command.ClearChatCommand;
import com.hanielcota.essentials.modules.clearchat.config.ClearChatConfig;
import com.hanielcota.essentials.modules.clearchat.service.ClearChatService;

public final class ClearChatModule extends AbstractModule {

  public ClearChatModule() {
    super("clearchat");
  }

  @Override
  protected void onEnable() {
    var config = config("clearchat", ClearChatConfig.class, ClearChatConfig::defaults);
    registerCommand(new ClearChatCommand(config, new ClearChatService()));
  }
}
