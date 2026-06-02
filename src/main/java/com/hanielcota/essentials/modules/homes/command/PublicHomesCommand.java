package com.hanielcota.essentials.modules.homes.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.homes.menu.PublicHomesMenu;
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

@Command("publichomes")
@EssentialsCommand
@PlayerOnly
@Permission("essentials.home.visit")
@Description("Abre o diretório de homes públicas e teleporta como visitante.")
@Syntax("/publichomes")
public record PublicHomesCommand(MenuService menus) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor) {
    var sender = actor.unwrap(Player.class);

    MenuOpenings.open(this.menus, sender, PublicHomesMenu.ID, actor);
    return CommandResult.success();
  }
}
