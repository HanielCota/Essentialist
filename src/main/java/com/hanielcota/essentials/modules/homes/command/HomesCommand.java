package com.hanielcota.essentials.modules.homes.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.menu.HomesMenu;
import com.hanielcota.essentials.modules.homes.menu.HomesMenuState;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("homes")
@EssentialsCommand
@Permission("essentials.home.list")
@Cooldown(duration = "3s")
@Description("Abre o menu de homes com teleporte, deletar, renomear e trocar ícone.")
@Syntax("/homes")
public record HomesCommand(
    ConfigHandle<HomesConfig> config,
    HomeService service,
    MenuService menus,
    HomesMenuState state) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor) {
    var sender = actor.unwrap(Player.class);
    var homes = this.service.list(sender.getUniqueId());

    if (homes.isEmpty()) {
      var messages = this.config.value().messages();
      var noHomesMsg = messages.noHomes();
      actor.sendError(noHomesMsg);
      return;
    }

    this.state.prefetch(sender.getUniqueId(), homes);
    MenuOpenings.open(this.menus, sender, HomesMenu.ID, actor);
  }
}
