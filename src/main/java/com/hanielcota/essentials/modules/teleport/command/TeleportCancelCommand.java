package com.hanielcota.essentials.modules.teleport.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.teleport.config.TeleportConfig;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("tpcancel")
@EssentialsCommand
@Permission("essentials.teleport.cancel")
@Description("Cancels the warm-up countdown of a pending teleport.")
@Syntax("/tpcancel")
public record TeleportCancelCommand(ConfigHandle<TeleportConfig> config, DelayedTeleport delayed) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor) {
    var sender = actor.unwrap(Player.class);
    var senderId = sender.getUniqueId();

    var cancelled = this.delayed.cancelAndNotify(senderId);
    if (cancelled) {
      return CommandResult.success();
    }

    var snap = this.config.value();
    var noPendingMsg = snap.cancelNoPending();
    return CommandResult.invalidUsage(noPendingMsg);
  }
}
