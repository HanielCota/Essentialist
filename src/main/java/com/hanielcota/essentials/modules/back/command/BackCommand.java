package com.hanielcota.essentials.modules.back.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.back.menu.BackMenu;
import com.hanielcota.essentials.modules.back.menu.BackMenuState;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("back")
@EssentialsCommand
@Permission("essentials.back")
@Cooldown(duration = "5s")
@Description("Retorna à localização anterior ou abre o histórico de teleportes.")
@Syntax("/back")
public record BackCommand(
    ConfigHandle<BackConfig> config,
    TeleportHistory history,
    MenuService menus,
    BackMenuState state) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor) {
    var sender = actor.unwrap(Player.class);
    var entries = this.history.list(sender.getUniqueId());
    if (entries.isEmpty()) {
      var noBackMsg = this.config.value().noBack();
      actor.sendError(noBackMsg);
      return;
    }

    this.state.prefetch(sender.getUniqueId(), entries);
    MenuOpenings.open(this.menus, sender, BackMenu.ID, actor);
  }
}
