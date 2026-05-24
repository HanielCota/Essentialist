package com.hanielcota.essentials.modules.teleport.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.teleport.config.TeleportConfig;
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
public record TeleportHereCommand(
    ConfigHandle<TeleportConfig> config, PaperCommandFramework framework) {

  @DefaultSubcommand
  public void execute(@NonNull Player sender, @OnlinePlayer @NonNull Player target) {
    var snap = this.config.value();
    var senderActor = this.framework.actorOf(sender);

    if (sender.getUniqueId().equals(target.getUniqueId())) {
      senderActor.sendError(snap.selfTarget());
      return;
    }

    var destination = sender.getLocation();
    var senderName = sender.getName();
    var targetName = target.getName();
    target
        .teleportAsync(destination)
        .thenAccept(
            success -> {
              if (!Boolean.TRUE.equals(success)) {
                senderActor.sendError(snap.teleportFailed());
                return;
              }
              var targetActor = this.framework.actorOf(target);
              senderActor.sendDualMessage(
                  targetActor,
                  snap.formatBroughtPlayer(targetName),
                  snap.formatBroughtBy(senderName));
            });
  }
}
