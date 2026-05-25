package com.hanielcota.essentials.modules.workstations.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.entity.Player;
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
public final class WorkbenchCommand {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor) {
    var player = actor.unwrap(Player.class);

    var menu = MenuType.CRAFTING.create(player);

    player.openInventory(menu);
  }
}
