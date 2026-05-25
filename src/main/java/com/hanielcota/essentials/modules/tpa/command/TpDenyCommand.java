package com.hanielcota.essentials.modules.tpa.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.paper.PlayerProvider;
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

@Command("tpdeny")
@EssentialsCommand
@Permission("essentials.tpa")
@Cooldown(duration = "1s")
@Description("Recusa um pedido de teleporte pendente.")
@Syntax("/tpdeny [jogador]")
public record TpDenyCommand(
    ConfigHandle<TpaConfig> config,
    TeleportRequestService service,
    ActorFactory actors,
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
    this.service.deny(request);

    var deniedSelfTemplate = messages.deniedSelf();
    var requesterName = request.requester().name();
    var deniedMsg = deniedSelfTemplate.replace("{player}", requesterName);

    actor.sendSuccess(deniedMsg);

    var deniedTemplate = messages.denied();
    TpaRequests.replyRequester(this.actors, this.players, request, deniedTemplate, false);
  }
}
