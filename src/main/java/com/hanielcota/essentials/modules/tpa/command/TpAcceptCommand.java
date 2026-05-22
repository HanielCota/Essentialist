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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@Command("tpaccept")
@EssentialsCommand
@Permission("essentials.tpa")
@Cooldown(duration = "1s")
@Description("Aceita um pedido de teleporte pendente.")
@Syntax("/tpaccept [jogador]")
public record TpAcceptCommand(
    ConfigHandle<TpaConfig> config,
    TeleportRequestService service,
    PaperCommandFramework framework) {

  @DefaultSubcommand
  public void execute(CommandActor actor, @DefaultValue("") String requester) {
    var messages = config.value().messages();
    Player sender = actor.unwrap(Player.class);

    var resolved = TpaRequests.resolveIncoming(service, sender, requester, messages, actor);
    if (resolved.isEmpty()) {
      return;
    }
    var request = resolved.get();

    switch (service.accept(request)) {
      case SUCCESS -> {
        actor.sendSuccess(messages.formatAcceptedSelf(request.requester().name()));
        var requesterPlayer = Bukkit.getPlayer(request.requester().id());
        if (requesterPlayer != null) {
          framework
              .actorOf(requesterPlayer)
              .sendSuccess(messages.formatAccepted(request.target().name()));
        }
      }
      case REQUESTER_OFFLINE ->
          actor.sendError(messages.formatRequesterOffline(request.requester().name()));
      case TELEPORT_FAILED -> actor.sendError(messages.teleportFailed());
      case NOT_FOUND -> actor.sendError(messages.noIncoming());
    }
  }
}
