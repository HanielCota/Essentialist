package com.hanielcota.essentials.modules.whitelist.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.whitelist.config.WhitelistConfig;
import com.hanielcota.essentials.modules.whitelist.menu.WhitelistMenu;
import com.hanielcota.essentials.modules.whitelist.service.WhitelistService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.Objects;
import org.bukkit.entity.Player;

@Command("whitelist")
@Permission("essentials.whitelist")
@Cooldown(duration = "3s")
@Description("Gerencia a whitelist do servidor.")
@Syntax("/whitelist [add|remove] [jogador]")
public record WhitelistCommand(
    ConfigHandle<WhitelistConfig> config, WhitelistService service, MenuService menus) {

  @DefaultSubcommand
  public void open(CommandActor actor) {
    Objects.requireNonNull(actor, "actor");
    if (!actor.isPlayer()) {
      actor.sendError(config.value().menuPlayerOnly());
      return;
    }
    menus.open(actor.unwrap(Player.class), WhitelistMenu.ID);
  }

  @Subcommand("add")
  public void add(CommandActor sender, @Arg("jogador") String name) {
    Objects.requireNonNull(sender, "sender");
    Objects.requireNonNull(name, "name");

    var snap = config.value();
    switch (service.add(name)) {
      case ADDED -> sender.sendSuccess(snap.formatAdded(name));
      case ALREADY_WHITELISTED -> sender.sendError(snap.formatAlreadyAdded(name));
      case UNKNOWN_PLAYER -> sender.sendError(snap.formatUnknownPlayer(name));
      default -> throw new IllegalStateException("Unexpected add result");
    }
  }

  @Subcommand("remove")
  public void remove(CommandActor sender, @Arg("jogador") String name) {
    Objects.requireNonNull(sender, "sender");
    Objects.requireNonNull(name, "name");

    var snap = config.value();
    if (service.remove(name)) {
      sender.sendSuccess(snap.formatRemoved(name));
      return;
    }
    sender.sendError(snap.formatNotWhitelisted(name));
  }
}
