package com.hanielcota.essentials.modules.warps.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.warps.menu.WarpsMenu;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("warps")
@EssentialsCommand
@Permission("essentials.warp.list")
@Description("Abre o menu de warps do servidor.")
@Syntax("/warps")
public record WarpsCommand(@NonNull MenuService menus) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor) {
    var player = actor.unwrap(Player.class);
    MenuOpenings.open(this.menus, player, WarpsMenu.ID, actor);
    return CommandResult.success();
  }
}
