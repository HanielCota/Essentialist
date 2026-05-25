package com.hanielcota.essentials.modules.tpa.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.config.TpaMessages;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.service.AcceptResult;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.paper.PlayerProvider;
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
    PaperCommandFramework framework,
    PlayerProvider players) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor, @DefaultValue("") String requester) {
    var snap = this.config.value();
    var messages = snap.messages();

    var sender = actor.unwrap(Player.class);

    var resolved = TpaRequests.resolveIncoming(this.service, sender, requester, messages, actor);
    if (resolved.isEmpty()) {
      return;
    }

    var request = resolved.get();
    var pending = this.service.accept(request);

    pending.thenAccept(result -> handleResult(result, request, messages, actor));
  }

  private void handleResult(
      @NonNull AcceptResult result,
      @NonNull TeleportRequest request,
      @NonNull TpaMessages messages,
      @NonNull CommandActor actor) {
    switch (result) {
      case SUCCESS -> handleSuccess(request, messages, actor);
      case REQUESTER_OFFLINE -> handleRequesterOffline(request, messages, actor);
      case TELEPORT_FAILED -> actor.sendError(messages.teleportFailed());
      case NOT_FOUND -> actor.sendError(messages.noIncoming());
    }
  }

  private void handleSuccess(
      @NonNull TeleportRequest request,
      @NonNull TpaMessages messages,
      @NonNull CommandActor actor) {
    var requesterName = request.requester().name();
    var acceptedSelfTemplate = messages.acceptedSelf();
    var acceptedMsg = acceptedSelfTemplate.replace("{player}", requesterName);

    actor.sendSuccess(acceptedMsg);

    var acceptedTemplate = messages.accepted();
    TpaRequests.replyRequester(this.framework, this.players, request, acceptedTemplate, true);
  }

  private void handleRequesterOffline(
      @NonNull TeleportRequest request,
      @NonNull TpaMessages messages,
      @NonNull CommandActor actor) {
    var requesterName = request.requester().name();
    var requesterOfflineTemplate = messages.requesterOffline();
    var offlineMsg = requesterOfflineTemplate.replace("{player}", requesterName);

    actor.sendError(offlineMsg);
  }
}
