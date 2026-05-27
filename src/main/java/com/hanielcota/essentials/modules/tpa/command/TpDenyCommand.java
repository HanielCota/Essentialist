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
import io.github.hanielcota.commandframework.core.CommandResult;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("tpdeny")
@EssentialsCommand
@Permission("essentials.tpa")
@Cooldown(duration = "1s")
@Description("Recusa um pedido de teleporte pendente.")
@Syntax("/tpdeny [jogador]")
public record TpDenyCommand(
    ConfigHandle<TpaConfig> config,
    TeleportRequestService service,
    TpaIncomingResolver incomingResolver,
    TpaRequestReplyNotifier replyNotifier) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor, Optional<String> requester) {
    var snap = this.config.value();
    var messages = snap.messages();

    var sender = actor.unwrap(Player.class);

    var resolved = this.incomingResolver.resolve(sender, requester.orElse(""), actor);
    if (resolved.isEmpty()) {
      return CommandResult.success();
    }

    var request = resolved.get();
    var denied = this.service.deny(request);
    if (!denied) {
      return CommandResult.invalidUsage(actor, messages.noIncoming());
    }

    var deniedSelfTemplate = messages.deniedSelf();
    var requesterName = request.requester().name();
    var deniedMsg = deniedSelfTemplate.replace("{player}", requesterName);

    actor.sendSuccess(deniedMsg);

    var deniedTemplate = messages.denied();
    this.replyNotifier.notifyDenied(request, deniedTemplate);

    return CommandResult.success();
  }
}
