package com.hanielcota.essentials.modules.whitelist.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.whitelist.config.WhitelistConfig;
import com.hanielcota.essentials.modules.whitelist.menu.WhitelistMenu;
import com.hanielcota.essentials.modules.whitelist.service.WhitelistService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("whitelist")
@Permission("essentials.whitelist")
@Description("Gerencia a whitelist do servidor.")
@Syntax("/whitelist [add|remove] [jogador]")
public record WhitelistCommand(
    ConfigHandle<WhitelistConfig> config, WhitelistService service, MenuService menus) {

  @DefaultSubcommand
  @PlayerOnly
  public CommandResult open(@NonNull CommandActor actor) {
    var viewer = actor.unwrap(Player.class);

    MenuOpenings.open(this.menus, viewer, WhitelistMenu.ID, actor);
    return CommandResult.success();
  }

  @Subcommand("add")
  public CommandResult add(@NonNull CommandActor sender, @Arg("jogador") String name) {
    var snap = this.config.value();
    var result = this.service.add(name);

    return switch (result) {
      case ADDED -> {
        var addedMsg = snap.messages().formatAdded(name);
        sender.sendSuccess(addedMsg);
        yield CommandResult.success();
      }
      case ALREADY_WHITELISTED ->
          CommandResult.invalidUsage(snap.messages().formatAlreadyAdded(name));
      case UNKNOWN_PLAYER -> CommandResult.invalidUsage(snap.messages().formatUnknownPlayer(name));
    };
  }

  @Subcommand("remove")
  public CommandResult remove(@NonNull CommandActor sender, @Arg("jogador") String name) {
    var snap = this.config.value();
    var removed = this.service.remove(name);

    if (removed) {
      var removedMsg = snap.messages().formatRemoved(name);
      sender.sendSuccess(removedMsg);
      return CommandResult.success();
    }

    var notWhitelistedMsg = snap.messages().formatNotWhitelisted(name);
    return CommandResult.invalidUsage(notWhitelistedMsg);
  }
}
