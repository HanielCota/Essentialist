package com.hanielcota.essentials.modules.seen;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.nick.service.NickService;
import com.hanielcota.essentials.modules.seen.command.SeenCommand;
import com.hanielcota.essentials.modules.seen.config.SeenConfig;
import com.hanielcota.essentials.modules.seen.service.SeenService;
import com.hanielcota.essentials.paper.PlayerProvider;
import lombok.NonNull;

public final class SeenModule extends AbstractModule {

  public SeenModule() {
    super("seen");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("seen", SeenConfig.class, SeenConfig::defaults);
    var players = env.service(PlayerProvider.class);
    var service = new SeenService(() -> env.findService(NickService.class), players);

    registrar.command(new SeenCommand(config, service));
  }
}
