package com.hanielcota.essentials.modules.workstations;

import com.hanielcota.essentials.module.AbstractModule;
import com.hanielcota.essentials.modules.workstations.command.AnvilCommand;
import com.hanielcota.essentials.modules.workstations.command.CartographyTableCommand;
import com.hanielcota.essentials.modules.workstations.command.GrindstoneCommand;
import com.hanielcota.essentials.modules.workstations.command.LoomCommand;
import com.hanielcota.essentials.modules.workstations.command.SmithingTableCommand;
import com.hanielcota.essentials.modules.workstations.command.StonecutterCommand;
import com.hanielcota.essentials.modules.workstations.command.WorkbenchCommand;

public final class WorkstationsModule extends AbstractModule {

  public WorkstationsModule() {
    super("workstations");
  }

  @Override
  protected void onEnable() {
    registerCommand(new WorkbenchCommand());
    registerCommand(new AnvilCommand());
    registerCommand(new GrindstoneCommand());
    registerCommand(new StonecutterCommand());
    registerCommand(new LoomCommand());
    registerCommand(new CartographyTableCommand());
    registerCommand(new SmithingTableCommand());
  }
}
