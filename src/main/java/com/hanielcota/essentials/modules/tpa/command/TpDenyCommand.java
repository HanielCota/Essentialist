package com.hanielcota.essentials.modules.tpa.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.menu.pending.TpaPendingMenu;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.paper.ActorFactory;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * /tpdeny — denies a pending request. With a nick, jumps straight to that request via {@link
 * TpaIncomingResolver}; without arguments, denies the sole pending request, opens the pending menu
 * when the viewer has several, or reports {@code noIncoming}.
 */
@Command("tpdeny")
@EssentialsCommand
@Permission("essentials.tpa")
@PlayerOnly
@Cooldown(duration = "1s")
@Description("Recusa um pedido de teleporte pendente.")
@Syntax("/tpdeny [jogador]")
public record TpDenyCommand(
    ConfigHandle<TpaConfig> config,
    TeleportRequestService service,
    TpaRequestReplyNotifier replyNotifier,
    TpaIncomingResolver resolver,
    ActorFactory actors,
    MenuService menus) {

  @DefaultSubcommand
  public CommandResult execute(
      @NonNull CommandActor actor, @Arg("jogador") Optional<String> requesterName) {
    var sender = actor.unwrap(Player.class);

    if (requesterName.isPresent()) {
      var resolved = this.resolver.resolve(sender, requesterName.get(), actor);
      resolved.ifPresent(request -> deny(sender, request));
      return CommandResult.success();
    }

    var senderId = sender.getUniqueId();
    var pending = this.service.incoming(senderId);
    if (pending.isEmpty()) {
      var messages = this.config.value().messages();
      return CommandResult.invalidUsage(messages.noIncoming());
    }
    if (pending.size() > 1) {
      MenuOpenings.open(this.menus, sender, TpaPendingMenu.ID, actor);
      return CommandResult.success();
    }

    var sole = pending.getFirst();
    deny(sender, sole);

    return CommandResult.success();
  }

  private void deny(@NonNull Player sender, @NonNull TeleportRequest request) {
    var actor = this.actors.actorOf(sender);
    var messages = this.config.value().messages();

    var denied = this.service.deny(request);
    if (!denied) {
      actor.sendError(messages.noIncoming());
      return;
    }

    var requesterName = request.requester().name();
    var deniedSelf = messages.deniedSelf().replace("{player}", requesterName);
    actor.sendSuccess(deniedSelf);

    this.replyNotifier.notifyDenied(request, messages.denied());
  }
}
