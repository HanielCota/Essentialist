package com.hanielcota.essentials.modules.title;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.title.command.TitleCommand;
import com.hanielcota.essentials.modules.title.config.TitleConfig;
import com.hanielcota.essentials.modules.title.service.TitleRequestParser;
import com.hanielcota.essentials.modules.title.service.TitleService;
import com.hanielcota.essentials.paper.PlayerProvider;
import lombok.NonNull;

public final class TitleModule extends AbstractModule {

  public TitleModule() {
    super("title");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("title", TitleConfig.class, TitleConfig::defaults);
    var players = env.service(PlayerProvider.class);
    var service = new TitleService(config, players);
    var parser = new TitleRequestParser(players);
    registrar.command(new TitleCommand(config, service, players, parser));
  }
}
