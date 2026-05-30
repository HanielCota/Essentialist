package com.hanielcota.essentials.modules.ban.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.ban.config.BanConfig;
import com.hanielcota.essentials.modules.ban.menu.BanPickerMenu;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("ban")
@Permission("essentials.ban")
@Description("Abre o menu de banimento.")
@Syntax("/ban")
public record BanCommand(ConfigHandle<BanConfig> config, MenuService menus) {

  @DefaultSubcommand
  public CommandResult open(@NonNull CommandActor actor) {
    var snap = this.config.value();

    if (!actor.isPlayer()) {
      var playerOnlyMsg = snap.menuPlayerOnly();

      actor.sendMessage(playerOnlyMsg);
      return CommandResult.success();
    }

    var player = actor.unwrap(Player.class);
    MenuOpenings.open(this.menus, player, BanPickerMenu.ID, actor);

    return CommandResult.success();
  }
}
