package com.hanielcota.essentials.modules.teleport.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.modules.teleport.service.TeleportService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.OnlinePlayer;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("tphere")
@EssentialsCommand
@Permission("essentials.tphere")
@Description("Teleporta um jogador até você.")
@Syntax("/tphere <jogador>")
public record TeleportHereCommand(
    ActorFactory actors,
    TeleportNotifier notifier,
    TeleportService teleport,
    MainThreadCallbacks callbacks) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull Player sender, @OnlinePlayer @NonNull Player target) {
    var senderActor = this.actors.actorOf(sender);
    var senderName = sender.getName();
    var targetName = target.getName();

    var future = this.teleport.bringHere(sender, target);
    this.callbacks.hop(
        future,
        outcome ->
            this.notifier.notifyBringHere(senderActor, target, senderName, targetName, outcome),
        "tphere");

    return CommandResult.success();
  }
}
