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
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import org.bukkit.entity.Player;

@Command("informacoes")
@EssentialsCommand
@Permission("essentials.info")
@Cooldown(duration = "3s")
@Description("Abre o painel de informações suas ou de outro jogador.")
@Syntax("/informacoes [jogador]")
public record InfoCommand(InfoMenu menu, MenuService menus) {

  @DefaultSubcommand
  public void execute(CommandActor actor, @TargetOrSelf Player target) {
    Player viewer = actor.unwrap(Player.class);
    menu.prepare(viewer.getUniqueId(), target.getUniqueId());
    menus.open(viewer, InfoMenu.ID);
  }
}
