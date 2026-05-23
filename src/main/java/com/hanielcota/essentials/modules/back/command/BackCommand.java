package com.hanielcota.essentials.modules.back.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.back.menu.BackMenu;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import org.bukkit.entity.Player;

@Command("back")
@EssentialsCommand
@Permission("essentials.back")
@Cooldown(duration = "5s")
@Description("Retorna à localização anterior ou abre o histórico de teleportes.")
@Syntax("/back")
public record BackCommand(
    ConfigHandle<BackConfig> config, TeleportHistory history, MenuService menus, BackMenu menu) {

  @DefaultSubcommand
  public void execute(CommandActor actor) {
    var sender = actor.unwrap(Player.class);
    var entries = history.list(sender.getUniqueId());
    if (entries.isEmpty()) {
      actor.sendError(config.value().noBack());
      return;
    }

    menu.prefetch(sender.getUniqueId(), entries);
    menus.open(sender, BackMenu.ID);
  }
}
