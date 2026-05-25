package com.hanielcota.essentials.modules.tpa.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.service.AcceptResult;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.DefaultValue;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
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
    TpAcceptResultHandler resultHandler,
    MainThreadCallbacks callbacks) {

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
    var claim = this.service.tryAccept(request);

    this.resultHandler.handleClaim(claim, request, actor);

    if (claim != AcceptResult.ACCEPTED) {
      return;
    }

    var pending = this.service.dispatchTeleport(request);
    this.callbacks.hop(
        pending,
        success -> this.resultHandler.handleTeleportOutcome(success, actor),
        "tpaccept dispatch");
  }
}
