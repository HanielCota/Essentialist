package com.hanielcota.essentials.modules.list.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.list.menu.ListMenu;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command(
    value = "list",
    aliases = {"who", "players"})
@Permission("essentials.list")
@Description("Abre o menu com os jogadores online agrupados.")
@Syntax("/list")
public record ListCommand(MenuService menus) {

  @DefaultSubcommand
  @PlayerOnly
  public CommandResult execute(@NonNull CommandActor actor) {
    var viewer = actor.unwrap(Player.class);

    MenuOpenings.open(this.menus, viewer, ListMenu.ID, actor);

    return CommandResult.success();
  }
}
