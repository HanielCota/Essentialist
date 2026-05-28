package com.hanielcota.essentials.modules.workstations.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.workstations.config.WorkstationsConfig;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.inventory.MenuType;

@Command(
    value = "bancada",
    aliases = {"workbench", "wb", "craft"})
@EssentialsCommand
@Permission("essentials.workbench")
@PlayerOnly
@Cooldown(duration = "2s")
@Description("Opens a virtual crafting table.")
@Syntax("/bancada")
public record WorkbenchCommand(ConfigHandle<WorkstationsConfig> config) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor) {
    WorkstationOpener.open(actor, MenuType.CRAFTING);

    return CommandResult.success();
  }
}
