package com.hanielcota.essentials.modules.give;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.give.command.GiveCommand;
import com.hanielcota.essentials.modules.give.command.GiveNotifier;
import com.hanielcota.essentials.modules.give.command.GiveOrchestrator;
import com.hanielcota.essentials.modules.give.config.GiveConfig;
import com.hanielcota.essentials.modules.give.service.GiveService;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;

public final class GiveModule extends AbstractModule {

  public GiveModule() {
    super("give");
  }

  @Override
  protected void onEnable() {
    var config = configure("give", GiveConfig.class, GiveConfig::defaults, new GiveService());
    var giveService = service(GiveService.class);
    var playerProvider = service(PlayerProvider.class);
    var framework = service(PaperCommandFramework.class);
    var notifier = new GiveNotifier(config, framework);
    var orchestrator = new GiveOrchestrator(config, giveService, notifier);
    var giveCommand = new GiveCommand(orchestrator, playerProvider);
    registerCommand(giveCommand);
  }
}
