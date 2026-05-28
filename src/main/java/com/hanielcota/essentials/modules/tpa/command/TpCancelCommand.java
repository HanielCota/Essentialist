package com.hanielcota.essentials.modules.tpa.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.tpa.menu.help.TpaHelpMenu;
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
import org.bukkit.entity.Player;

@Command("tpacancel")
@EssentialsCommand
@Permission("essentials.tpa")
@PlayerOnly
@Cooldown(duration = "1s")
@Description("Abre o menu de TPA — o slot \"Pedido enviado\" tem o botão de cancelar.")
@Syntax("/tpacancel")
public record TpCancelCommand(MenuService menus) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor) {
    var sender = actor.unwrap(Player.class);

    MenuOpenings.open(this.menus, sender, TpaHelpMenu.ID, actor);

    return CommandResult.success();
  }
}
