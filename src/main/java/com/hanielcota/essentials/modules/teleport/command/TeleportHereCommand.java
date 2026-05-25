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
import io.github.hanielcota.commandframework.core.CommandActor;
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

    var senderId = sender.getUniqueId();
    var targetId = target.getUniqueId();
    if (senderId.equals(targetId)) {
      var selfTargetMsg = snap.selfTarget();
      senderActor.sendError(selfTargetMsg);
      return;
    }

    var destination = sender.getLocation();
    var senderName = sender.getName();
    var targetName = target.getName();

    var teleport = target.teleportAsync(destination);
    teleport.thenAccept(
        success -> onTeleportComplete(success, snap, senderActor, target, senderName, targetName));
  }

  private void onTeleportComplete(
      Boolean success,
      TeleportConfig snap,
      CommandActor senderActor,
      Player target,
      String senderName,
      String targetName) {
    if (!Boolean.TRUE.equals(success)) {
      var failedMsg = snap.teleportFailed();
      senderActor.sendError(failedMsg);
      return;
    }

    var targetActor = this.framework.actorOf(target);
    var senderMsg = snap.formatBroughtPlayer(targetName);
    var targetMsg = snap.formatBroughtBy(senderName);

    senderActor.sendDualMessage(targetActor, senderMsg, targetMsg);
  }
}
