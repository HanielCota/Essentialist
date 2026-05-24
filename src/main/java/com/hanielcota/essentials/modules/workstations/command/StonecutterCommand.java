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

@Command(value = "cortador", aliases = "stonecutter")
@EssentialsCommand
@Permission("essentials.stonecutter")
@PlayerOnly
@Cooldown(duration = "2s")
@Description("Opens a virtual stonecutter.")
@Syntax("/cortador")
public final class StonecutterCommand {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor) {
    Player player = actor.unwrap(Player.class);
    player.openInventory(MenuType.STONECUTTER.create(player));
  }
}
