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
import lombok.NonNull;
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
  public void execute(@NonNull CommandActor actor, @DefaultValue("") String requester) {
    var messages = this.config.value().messages();
    var sender = actor.unwrap(Player.class);

    var resolved = TpaRequests.resolveIncoming(this.service, sender, requester, messages, actor);
    if (resolved.isEmpty()) {
      return;
    }
    var request = resolved.get();

    this.service
        .accept(request)
        .thenAccept(
            result -> {
              switch (result) {
                case SUCCESS -> {
                  var requesterName = request.requester().name();
                  var acceptedSelfTemplate = messages.acceptedSelf();
                  var acceptedMsg = acceptedSelfTemplate.replace("{player}", requesterName);
                  actor.sendSuccess(acceptedMsg);

                  TpaRequests.replyRequester(this.framework, request, messages.accepted(), true);
                }
                case REQUESTER_OFFLINE -> {
                  var requesterName = request.requester().name();
                  var requesterOfflineTemplate = messages.requesterOffline();
                  var offlineMsg = requesterOfflineTemplate.replace("{player}", requesterName);
                  actor.sendError(offlineMsg);
                }
                case TELEPORT_FAILED -> actor.sendError(messages.teleportFailed());
                case NOT_FOUND -> actor.sendError(messages.noIncoming());
              }
            });
  }
}
