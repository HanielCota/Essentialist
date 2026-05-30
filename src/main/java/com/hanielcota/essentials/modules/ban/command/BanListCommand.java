package com.hanielcota.essentials.modules.ban.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.ban.config.BanConfig;
import com.hanielcota.essentials.modules.ban.menu.BanListMenu;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("banlist")
@Permission("essentials.ban")
@Description("Abre o menu de bans ativos (clique para desbanir).")
@Syntax("/banlist")
public record BanListCommand(ConfigHandle<BanConfig> config, MenuService menus) {

  @DefaultSubcommand
  public CommandResult open(@NonNull CommandActor actor) {
    var snap = this.config.value();

    if (!actor.isPlayer()) {
      var playerOnlyMsg = snap.menuPlayerOnly();

      actor.sendMessage(playerOnlyMsg);
      return CommandResult.success();
    }

    var player = actor.unwrap(Player.class);
    MenuOpenings.open(this.menus, player, BanListMenu.ID, actor);

    return CommandResult.success();
  }
}
