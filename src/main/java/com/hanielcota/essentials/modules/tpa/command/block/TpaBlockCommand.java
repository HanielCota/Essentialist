package com.hanielcota.essentials.modules.tpa.command.block;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.menu.TpaBlockedMenu;
import com.hanielcota.essentials.modules.tpa.service.TpaBlockService;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.annotation.Command;
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
 * /tpablock — when called with a nick, blocks the player and opens the blocked menu so the user
 * sees the updated list; with no nick, just opens the menu. The arg-form remains the only way to
 * add a new block since {@code TpaBlockedMenu} only surfaces unblock (no add-block UI).
 */
@Command("tpablock")
@EssentialsCommand
@Permission("essentials.tpa")
@PlayerOnly
@Description("Abre o menu de bloqueados; com nick, bloqueia o jogador antes de abrir.")
@Syntax("/tpablock [jogador]")
public record TpaBlockCommand(
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
    var senderId = sender.getUniqueId();
    var targetId = target.getUniqueId();

    if (senderId.equals(targetId)) {
      return CommandResult.invalidUsage(messages.blockSelf());
    }

    var blockedName = target.getName() != null ? target.getName() : name;
    this.blocks.block(senderId, targetId, blockedName);

    actor.sendSuccess(messages.blockedPlayer().replace("{player}", blockedName));

    MenuOpenings.open(this.menus, sender, TpaBlockedMenu.ID, actor);

    return CommandResult.success();
  }
}
