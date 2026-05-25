package com.hanielcota.essentials.modules.whitelist.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.whitelist.config.WhitelistConfig;
import com.hanielcota.essentials.modules.whitelist.menu.WhitelistMenu;
import com.hanielcota.essentials.modules.whitelist.service.WhitelistService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("whitelist")
@Permission("essentials.whitelist")
@Cooldown(duration = "3s")
@Description("Gerencia a whitelist do servidor.")
@Syntax("/whitelist [add|remove] [jogador]")
public record WhitelistCommand(
    ConfigHandle<WhitelistConfig> config, WhitelistService service, MenuService menus) {

  @DefaultSubcommand
  @PlayerOnly
  public void open(@NonNull CommandActor actor) {
    var viewer = actor.unwrap(Player.class);

    MenuOpenings.open(this.menus, viewer, WhitelistMenu.ID, actor);
  }

  @Subcommand("add")
  public void add(@NonNull CommandActor sender, @Arg("jogador") String name) {
    var snap = this.config.value();
    var result = this.service.add(name);

    switch (result) {
      case ADDED -> {
        var addedMsg = snap.formatAdded(name);
        sender.sendSuccess(addedMsg);
      }
      case ALREADY_WHITELISTED -> {
        var alreadyAddedMsg = snap.formatAlreadyAdded(name);
        sender.sendError(alreadyAddedMsg);
      }
      case UNKNOWN_PLAYER -> {
        var unknownMsg = snap.formatUnknownPlayer(name);
        sender.sendError(unknownMsg);
      }
    }
  }

  @Subcommand("remove")
  public void remove(@NonNull CommandActor sender, @Arg("jogador") String name) {
    var snap = this.config.value();
    var removed = this.service.remove(name);

    if (removed) {
      var removedMsg = snap.formatRemoved(name);
      sender.sendSuccess(removedMsg);
      return;
    }

    var notWhitelistedMsg = snap.formatNotWhitelisted(name);
    sender.sendError(notWhitelistedMsg);
  }
}
