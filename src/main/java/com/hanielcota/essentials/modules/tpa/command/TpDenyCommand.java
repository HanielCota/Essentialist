package com.hanielcota.essentials.modules.tpa.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.DefaultValue;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
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
    PaperCommandFramework framework) {

  @DefaultSubcommand
  public void execute(CommandActor actor, @DefaultValue("") String requester) {
    var messages = config.value().messages();
    var sender = actor.unwrap(Player.class);

    var resolved = TpaRequests.resolveIncoming(service, sender, requester, messages, actor);
    if (resolved.isEmpty()) {
      return;
    }
    var request = resolved.get();

    service.deny(request);
    actor.sendSuccess(messages.deniedSelf().replace("{player}", request.requester().name()));
    TpaRequests.replyRequester(framework, request, messages.denied(), false);
  }
}
