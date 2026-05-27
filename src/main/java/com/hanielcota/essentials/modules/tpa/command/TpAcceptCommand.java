package com.hanielcota.essentials.modules.tpa.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.AcceptOutcome;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.menu.pending.TpaPendingMenu;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
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
 * /tpaccept — accepts a pending request. With a nick, jumps straight to that request via {@link
 * TpaIncomingResolver}; without arguments, accepts the sole pending request, opens the pending menu
 * when the viewer has several, or reports {@code noIncoming} when there is nothing to accept.
 * Mirrors the click handler in {@code TpaPendingActionMenu}.
 */
@Command("tpaccept")
@EssentialsCommand
@Permission("essentials.tpa")
@PlayerOnly
@Cooldown(duration = "1s")
@Description("Aceita um pedido de teleporte pendente.")
@Syntax("/tpaccept [jogador]")
public record TpAcceptCommand(
    ConfigHandle<TpaConfig> config,
    TeleportRequestService service,
    TpAcceptOutcomeHandler acceptHandler,
    TpaIncomingResolver resolver,
    MainThreadCallbacks callbacks,
    ActorFactory actors,
    MenuService menus) {

  @DefaultSubcommand
  public CommandResult execute(
      @NonNull CommandActor actor, @Arg("jogador") Optional<String> requesterName) {
    var sender = actor.unwrap(Player.class);

    if (requesterName.isPresent()) {
      var resolved = this.resolver.resolve(sender, requesterName.get(), actor);
      resolved.ifPresent(request -> accept(sender, request));
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
    accept(sender, sole);

    return CommandResult.success();
  }

  private void accept(@NonNull Player sender, @NonNull TeleportRequest request) {
    var actor = this.actors.actorOf(sender);
    var claim = this.service.tryAccept(request);
    this.acceptHandler.handleClaim(claim, request, actor);

    if (claim != AcceptOutcome.ACCEPTED) {
      return;
    }

    var pendingTeleport = this.service.dispatchTeleport(request);
    this.callbacks.hop(
        pendingTeleport,
        success -> this.acceptHandler.handleTeleportOutcome(success, actor),
        "/tpaccept");
  }
}
