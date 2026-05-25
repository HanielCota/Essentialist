package com.hanielcota.essentials.modules.clear;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.clear.command.ClearCommand;
import com.hanielcota.essentials.modules.clear.config.ClearConfig;
import com.hanielcota.essentials.modules.clear.service.ClearService;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
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

    var commandFramework = env.service(PaperCommandFramework.class);

    var clearCommand = new ClearCommand(configHandle, clearService, commandFramework);
    registrar.command(clearCommand);
  }
}
