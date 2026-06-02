package com.hanielcota.essentials.modules.skull.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.modules.skull.service.SkullService;
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
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@Command("skull")
@EssentialsCommand
@Permission("essentials.skull")
@PlayerOnly
@Description("Recebe a cabeça de um jogador.")
@Syntax("/skull [jogador]")
public record SkullCommand(
    SkullNotifier notifier, SkullService skullService, PlayerProvider players) {

  @DefaultSubcommand
  public CommandResult execute(
      CommandActor actor, @Suggestions("players") Optional<String> targetName) {
    var recipient = actor.unwrap(Player.class);

    if (targetName.isEmpty()) {
      return giveSkull(actor, recipient, recipient);
    }

    var name = targetName.get();
    var resolved = players.offlineByName(name);

    if (resolved.isEmpty()) {
      notifier.sendPlayerNotFound(actor);
      return CommandResult.invalidUsage();
    }

    var owner = resolved.get();
    return giveSkull(actor, recipient, owner);
  }

  private CommandResult giveSkull(CommandActor actor, Player recipient, OfflinePlayer owner) {
    var delivery = skullService.deliver(recipient, owner);

    if (!delivery.isSuccess()) {
      notifier.sendInventoryFull(actor);
      return CommandResult.invalidUsage();
    }

    var self = Senders.isSelf(actor, owner);
    notifier.sendReceived(actor, owner, self);
    return CommandResult.success();
  }
}
