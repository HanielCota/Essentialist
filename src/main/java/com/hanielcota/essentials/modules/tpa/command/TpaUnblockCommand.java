package com.hanielcota.essentials.modules.tpa.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.menu.TpaBlockedMenu;
import com.hanielcota.essentials.modules.tpa.service.TpaBlockService;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Suggestions;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * /tpaunblock — with a nick, unblocks the player and opens the blocked menu so the user sees the
 * updated list; with no nick, just opens the menu (where unblock is also available via click).
 */
@Command("tpaunblock")
@EssentialsCommand
@Permission("essentials.tpa")
@PlayerOnly
@Cooldown(duration = "1s")
@Description("Abre o menu de bloqueados; com nick, desbloqueia o jogador antes de abrir.")
@Syntax("/tpaunblock [jogador]")
public record TpaUnblockCommand(
    ConfigHandle<TpaConfig> config,
    TpaBlockService blocks,
    PlayerProvider players,
    MenuService menus) {

  @DefaultSubcommand
  public CommandResult execute(
      @NonNull CommandActor actor, @Suggestions("players") Optional<String> targetName) {
    var sender = actor.unwrap(Player.class);

    if (targetName.isEmpty()) {
      MenuOpenings.open(this.menus, sender, TpaBlockedMenu.ID, actor);
      return CommandResult.success();
    }

    var messages = this.config.value().messages();
    var name = targetName.get();
    var resolved = this.players.offlineByName(name);

    if (resolved.isEmpty()) {
      return CommandResult.invalidUsage(messages.playerNotFound().replace("{player}", name));
    }

    var target = resolved.get();
    var targetId = target.getUniqueId();
    var targetDisplayName = target.getName() != null ? target.getName() : name;

    this.blocks.unblock(sender.getUniqueId(), targetId);

    actor.sendSuccess(messages.unblockedPlayer().replace("{player}", targetDisplayName));

    MenuOpenings.open(this.menus, sender, TpaBlockedMenu.ID, actor);

    return CommandResult.success();
  }
}
