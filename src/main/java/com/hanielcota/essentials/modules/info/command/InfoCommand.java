package com.hanielcota.essentials.modules.info.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.modules.info.menu.InfoMenu;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.Objects;
import org.bukkit.entity.Player;

@Command("info")
@EssentialsCommand
@Permission("essentials.info")
@Cooldown(duration = "3s")
@Description("Abre o painel de informações.")
@Syntax("/info")
public record InfoCommand(MenuService menus) {

  @DefaultSubcommand
  public void execute(CommandActor actor) {
    Objects.requireNonNull(actor, "actor");
    menus.open(actor.unwrap(Player.class), InfoMenu.ID);
  }
}
