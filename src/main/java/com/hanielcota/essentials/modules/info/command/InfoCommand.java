package com.hanielcota.essentials.modules.info.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.info.menu.InfoMenu;
import com.hanielcota.essentials.modules.info.menu.InfoMenuState;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PermissionForOther;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command(value = "informacoes", aliases = "info")
@EssentialsCommand
@Permission("essentials.info")
@PermissionForOther(".others")
@PlayerOnly
@Cooldown(duration = "3s")
@Description("Abre o painel de informações suas ou de outro jogador.")
@Syntax("/informacoes [jogador]")
public record InfoCommand(InfoMenuState state, MenuService menus) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor, @TargetOrSelf @NonNull Player target) {
    var viewer = actor.unwrap(Player.class);
    var viewerId = viewer.getUniqueId();
    var targetId = target.getUniqueId();

    this.state.prepare(viewerId, targetId);

    MenuOpenings.open(this.menus, viewer, InfoMenu.ID, actor);

    return CommandResult.success();
  }
}
