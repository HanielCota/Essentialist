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
import io.github.hanielcota.commandframework.core.CommandResult;
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
  public CommandResult execute(@NonNull CommandActor actor) {
    var sender = actor.unwrap(Player.class);
    var uuid = sender.getUniqueId();
    var homes = this.service.homesOf(uuid);

    if (homes.isEmpty()) {
      var snap = this.config.value();
      var messages = snap.messages();
      var noHomesMsg = messages.noHomes();
      return CommandResult.invalidUsage(actor, noHomesMsg);
    }

    this.state.prefetch(uuid, homes);

    MenuOpenings.open(this.menus, sender, HomesMenu.ID, actor);
    return CommandResult.success();
  }
}
