package com.hanielcota.essentials.modules.tpa.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.paper.ActorFactory;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * /tpacancel — cancels the sender's outgoing /tpa, notifying the target so the stale clickable
 * prompt is explained. Reports {@code noOutgoing} when there is nothing to cancel instead of
 * opening the hub menu.
 */
@Command("tpacancel")
@EssentialsCommand
@Permission("essentials.tpa")
@PlayerOnly
@Cooldown(duration = "1s")
@Description("Cancela seu pedido de teleporte enviado.")
@Syntax("/tpacancel")
public record TpCancelCommand(
    ConfigHandle<TpaConfig> config,
    TeleportRequestService service,
    TpaNotifier notifier,
    ActorFactory actors) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor) {
    var sender = actor.unwrap(Player.class);
    var senderId = sender.getUniqueId();
    var messages = this.config.value().messages();

    var outgoing = this.service.outgoing(senderId);
    if (outgoing.isEmpty()) {
      return CommandResult.invalidUsage(messages.noOutgoing());
    }

    var request = outgoing.get();
    var cancelled = this.service.cancel(request);
    if (!cancelled) {
      return CommandResult.invalidUsage(messages.noOutgoing());
    }

    this.notifier.notifyCancelledByRequester(request);

    var senderActor = this.actors.actorOf(sender);
    var targetName = request.target().name();
    var cancelledText = messages.cancelled().replace("{player}", targetName);
    senderActor.sendSuccess(cancelledText);

    return CommandResult.success();
  }
}
