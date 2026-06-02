package com.hanielcota.essentials.modules.skull;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.skull.command.SkullCommand;
import com.hanielcota.essentials.modules.skull.command.SkullNotifier;
import com.hanielcota.essentials.modules.skull.config.SkullConfig;
import com.hanielcota.essentials.modules.skull.service.SkullService;
import com.hanielcota.essentials.paper.PlayerProvider;
import lombok.NonNull;

public final class SkullModule extends AbstractModule {

  public SkullModule() {
    super("skull");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("skull", SkullConfig.class, SkullConfig::defaults);
    var notifier = new SkullNotifier(config);
    var skullService = new SkullService();
    var players = env.service(PlayerProvider.class);
    var command = new SkullCommand(notifier, skullService, players);

    registrar.command(command);
  }
}
