package com.hanielcota.essentials.modules.teleport.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.modules.teleport.service.TeleportService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.OnlinePlayer;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("tphere")
@EssentialsCommand
@Permission("essentials.tphere")
@Cooldown(duration = "3s")
@Description("Teleporta um jogador até você.")
@Syntax("/tphere <jogador>")
public record TeleportHereCommand(PaperCommandFramework framework, TeleportNotifier notifier) {

  @DefaultSubcommand
  public void execute(@NonNull Player sender, @OnlinePlayer @NonNull Player target) {
    var senderActor = this.framework.actorOf(sender);
    var senderName = sender.getName();
    var targetName = target.getName();

    TeleportService.bringHere(sender, target)
        .thenAccept(
            outcome ->
                this.notifier.notifyBringHere(
                    senderActor, target, senderName, targetName, outcome));
  }
}
