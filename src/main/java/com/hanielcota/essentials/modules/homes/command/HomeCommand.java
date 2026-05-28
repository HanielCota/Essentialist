package com.hanielcota.essentials.modules.homes.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.menu.HomesMenu;
import com.hanielcota.essentials.modules.homes.menu.HomesMenuState;
import com.hanielcota.essentials.modules.homes.service.HomeNameResolver;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.modules.homes.service.HomeTeleporter;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("home")
@EssentialsCommand
@Permission("essentials.home.use")
@Description("Teleporta para a home indicada ou abre o menu /homes quando sem argumento.")
@Syntax("/home [nome]")
public record HomeCommand(
    ConfigHandle<HomesConfig> config,
    HomeService service,
    HomeTeleporter teleporter,
    HomeNameResolver nameResolver,
    MissingHomeMessageResolver missingResolver,
    MenuService menus,
    HomesMenuState state) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor, @Arg("nome") Optional<String> rawName) {
    var sender = actor.unwrap(Player.class);

    if (rawName.isEmpty()) {
      return openMenu(sender, actor);
    }

    var snap = this.config.value();
    var messages = snap.messages();
    var name = this.nameResolver.resolve(rawName.get());

    if (name == null) {
      var invalidNameMsg = messages.invalidName();
      return CommandResult.invalidUsage(invalidNameMsg);
    }

    var uuid = sender.getUniqueId();
    var home = this.service.findHome(uuid, name);

    if (home.isEmpty()) {
      var missingMsg = this.missingResolver.resolve(uuid, name);
      return CommandResult.invalidUsage(missingMsg);
    }

    var target = home.get();

    this.teleporter.teleport(sender, target, actor);
    return CommandResult.success();
  }

  private CommandResult openMenu(@NonNull Player sender, @NonNull CommandActor actor) {
    var uuid = sender.getUniqueId();
    var homes = this.service.homesOf(uuid);

    this.state.prefetch(uuid, homes);

    MenuOpenings.open(this.menus, sender, HomesMenu.ID, actor);
    return CommandResult.success();
  }
}
