package com.hanielcota.essentials.modules.workstations;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.environment.ModuleEnvironment;
import com.hanielcota.essentials.module.registration.ModuleRegistrar;
import com.hanielcota.essentials.modules.workstations.command.AnvilCommand;
import com.hanielcota.essentials.modules.workstations.command.CartographyTableCommand;
import com.hanielcota.essentials.modules.workstations.command.GrindstoneCommand;
import com.hanielcota.essentials.modules.workstations.command.LoomCommand;
import com.hanielcota.essentials.modules.workstations.command.SmithingTableCommand;
import com.hanielcota.essentials.modules.workstations.command.StonecutterCommand;
import com.hanielcota.essentials.modules.workstations.command.WorkbenchCommand;
import com.hanielcota.essentials.modules.workstations.config.WorkstationsConfig;
import lombok.NonNull;

public final class WorkstationsModule extends AbstractModule {

  public WorkstationsModule() {
    super("workstations");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    var config = env.config("workstations", WorkstationsConfig.class, WorkstationsConfig::defaults);

    registrar.command(new WorkbenchCommand(config));
    registrar.command(new AnvilCommand(config));
    registrar.command(new GrindstoneCommand(config));
    registrar.command(new StonecutterCommand(config));
    registrar.command(new LoomCommand(config));
    registrar.command(new CartographyTableCommand(config));
    registrar.command(new SmithingTableCommand(config));
  }
}
