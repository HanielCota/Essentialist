package com.hanielcota.essentials.modules.clear;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.clear.command.ClearCommand;
import com.hanielcota.essentials.modules.clear.config.ClearConfig;
import com.hanielcota.essentials.modules.clear.service.ClearService;
import com.hanielcota.essentials.paper.ActorFactory;
import lombok.NonNull;

public final class ClearModule extends AbstractModule {

  public ClearModule() {
    super("clear");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var defaultValues = ClearConfig.defaults();
    var clearService = new ClearService();

    var configHandle =
        registrar.configure("clear", ClearConfig.class, () -> defaultValues, clearService);

    var actors = env.service(ActorFactory.class);

    var clearCommand = new ClearCommand(configHandle, clearService, actors);
    registrar.command(clearCommand);
  }
}
