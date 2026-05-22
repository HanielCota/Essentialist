package com.hanielcota.essentials.modules.online.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.modules.online.menu.OnlineMenu;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import org.bukkit.entity.Player;

@Command("online")
@EssentialsCommand
@Permission("essentials.online")
@Cooldown(duration = "3s")
@Description("Mostra os jogadores online em um menu.")
@Syntax("/online")
public record OnlineCommand(MenuService menus) {

  @DefaultSubcommand
  public void execute(CommandActor actor) {
    Player player = actor.unwrap(Player.class);
    menus.open(player, OnlineMenu.id);
  }
}
