package com.hanielcota.essentials.modules.give;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.give.command.GiveCommand;
import com.hanielcota.essentials.modules.give.command.GiveNotifier;
import com.hanielcota.essentials.modules.give.command.GiveOrchestrator;
import com.hanielcota.essentials.modules.give.config.GiveConfig;
import com.hanielcota.essentials.modules.give.service.GiveService;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;

public final class GiveModule extends AbstractModule {

  public GiveModule() {
    super("give");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var giveService = new GiveService();
    var config = registrar.configure("give", GiveConfig.class, GiveConfig::defaults, giveService);
    var playerProvider = env.service(PlayerProvider.class);
    var framework = env.service(PaperCommandFramework.class);
    var notifier = new GiveNotifier(config, framework);
    var orchestrator = new GiveOrchestrator(config, giveService, notifier);
    var giveCommand = new GiveCommand(orchestrator, playerProvider);
    registrar.command(giveCommand);
  }
}
