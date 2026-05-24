package com.hanielcota.essentials.modules.tpa.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("tpacancel")
@EssentialsCommand
@Permission("essentials.tpa")
@Cooldown(duration = "1s")
@Description("Cancela o seu pedido de teleporte pendente.")
@Syntax("/tpacancel")
public record TpCancelCommand(ConfigHandle<TpaConfig> config, TeleportRequestService service) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor) {
    var messages = this.config.value().messages();
    var sender = actor.unwrap(Player.class);

    var pending = this.service.outgoing(sender.getUniqueId());
    if (pending.isEmpty()) {
      actor.sendError(messages.noOutgoing());
      return;
    }

    var request = pending.get();
    this.service.cancel(request);

    var cancelledTemplate = messages.cancelled();
    var targetName = request.target().name();

    var cancelledMsg = cancelledTemplate.replace("{player}", targetName);
    actor.sendSuccess(cancelledMsg);
  }
}
