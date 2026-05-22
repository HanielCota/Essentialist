package com.hanielcota.essentials.modules.teleport.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.teleport.config.TeleportMessages;
import com.hanielcota.essentials.modules.teleport.service.TeleportService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.OnlinePlayer;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import org.bukkit.entity.Player;

@Command("tphere")
@EssentialsCommand
@Permission("essentials.tphere")
@Cooldown(duration = "3s")
@Description("Teleporta um jogador até você.")
@Syntax("/tphere <jogador>")
public record TeleportHereCommand(
    ConfigHandle<TeleportMessages> config,
    TeleportService service,
    PaperCommandFramework framework) {

  @DefaultSubcommand
  public void execute(Player sender, @OnlinePlayer Player target) {
    var snap = config.value();
    var senderActor = framework.actorOf(sender);

    if (sender.getUniqueId().equals(target.getUniqueId())) {
      senderActor.sendError(snap.selfTarget());
      return;
    }

    if (!service.teleportTo(target, sender.getLocation())) {
      senderActor.sendError(snap.teleportFailed());
      return;
    }

    var targetActor = framework.actorOf(target);
    String selfMessage = snap.formatBroughtPlayer(target.getName());
    String otherMessage = snap.formatBroughtBy(sender.getName());
    senderActor.sendDualMessage(targetActor, selfMessage, otherMessage);
  }
}
