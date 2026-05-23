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
    this.menus.open(actor.unwrap(Player.class), WhitelistMenu.ID);
  }

  @Subcommand("add")
  public void add(@NonNull CommandActor sender, @Arg("jogador") String name) {
    var snap = this.config.value();
    switch (this.service.add(name)) {
      case ADDED -> sender.sendSuccess(snap.formatAdded(name));
      case ALREADY_WHITELISTED -> sender.sendError(snap.formatAlreadyAdded(name));
      case UNKNOWN_PLAYER -> sender.sendError(snap.formatUnknownPlayer(name));
    }
  }

  @Subcommand("remove")
  public void remove(@NonNull CommandActor sender, @Arg("jogador") String name) {
    var snap = this.config.value();
    if (this.service.remove(name)) {
      sender.sendSuccess(snap.formatRemoved(name));
      return;
    }
    sender.sendError(snap.formatNotWhitelisted(name));
  }
}
