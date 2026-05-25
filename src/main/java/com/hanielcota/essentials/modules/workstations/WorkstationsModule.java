package com.hanielcota.essentials.modules.workstations;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.module.ModuleEnvironment;
import com.hanielcota.essentials.module.ModuleRegistrar;
import com.hanielcota.essentials.modules.workstations.command.AnvilCommand;
import com.hanielcota.essentials.modules.workstations.command.CartographyTableCommand;
import com.hanielcota.essentials.modules.workstations.command.GrindstoneCommand;
import com.hanielcota.essentials.modules.workstations.command.LoomCommand;
import com.hanielcota.essentials.modules.workstations.command.SmithingTableCommand;
import com.hanielcota.essentials.modules.workstations.command.StonecutterCommand;
import com.hanielcota.essentials.modules.workstations.command.WorkbenchCommand;
import lombok.NonNull;

public final class WorkstationsModule extends AbstractModule {

  public WorkstationsModule() {
    super("workstations");
  }

  @Override
  protected void onEnable(@NonNull ModuleEnvironment env, @NonNull ModuleRegistrar registrar) {
    registrar.command(new WorkbenchCommand());
    registrar.command(new AnvilCommand());
    registrar.command(new GrindstoneCommand());
    registrar.command(new StonecutterCommand());
    registrar.command(new LoomCommand());
    registrar.command(new CartographyTableCommand());
    registrar.command(new SmithingTableCommand());
  }
}
