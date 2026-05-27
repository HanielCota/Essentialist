package com.hanielcota.essentials.modules.clearchat;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.clearchat.command.ClearChatCommand;
import com.hanielcota.essentials.modules.clearchat.config.ClearChatConfig;
import com.hanielcota.essentials.modules.clearchat.service.ClearChatService;
import com.hanielcota.essentials.paper.AudienceProvider;
import lombok.NonNull;

public final class ClearChatModule extends AbstractModule {

  public ClearChatModule() {
    super("clearchat");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("clearchat", ClearChatConfig.class, ClearChatConfig::defaults);
    var audiences = env.service(AudienceProvider.class);
    var service = new ClearChatService(audiences);
    var command = new ClearChatCommand(config, service);

    registrar.command(command);
  }
}
