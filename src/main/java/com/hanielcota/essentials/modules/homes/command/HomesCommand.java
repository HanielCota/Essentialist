package com.hanielcota.essentials.modules.homes.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.homes.menu.HomesMenu;
import com.hanielcota.essentials.modules.homes.menu.HomesMenuState;
import com.hanielcota.essentials.modules.homes.service.HomeService;
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

@Command("homes")
@EssentialsCommand
@PlayerOnly
@Permission("essentials.home.list")
@Description("Abre o menu de homes com teleporte, deletar, renomear e trocar ícone.")
@Syntax("/homes")
public record HomesCommand(HomeService service, MenuService menus, HomesMenuState state) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor) {
    var sender = actor.unwrap(Player.class);
    var uuid = sender.getUniqueId();
    var homes = this.service.homesOf(uuid);

    this.state.prefetch(uuid, homes);

    MenuOpenings.open(this.menus, sender, HomesMenu.ID, actor);
    return CommandResult.success();
  }
}
