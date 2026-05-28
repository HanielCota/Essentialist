package com.hanielcota.essentials.modules.tpa.command.accept;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.tpa.command.send.TpaIncomingResolver;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.AcceptOutcome;
import com.hanielcota.essentials.modules.tpa.menu.pending.TpaPendingMenu;
import com.hanielcota.essentials.modules.tpa.service.request.TeleportRequestService;
import com.hanielcota.essentials.paper.ActorFactory;
import com.hanielcota.essentials.scheduler.MainThreadCallbacks;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Suggestions;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("tpaccept")
@EssentialsCommand
@Permission("essentials.tpa")
@PlayerOnly
@Cooldown(duration = "1s")
@Description("Aceita um pedido de teleporte (ou abre o menu se nenhum nick for passado).")
@Syntax("/tpaccept [jogador]")
public record TpAcceptCommand(
    ConfigHandle<TpaConfig> config,
    MenuService menus,
    TeleportRequestService service,
    TpAcceptOutcomeHandler acceptHandler,
    TpAcceptTeleportNotifier teleportNotifier,
    TpaIncomingResolver resolver,
    ActorFactory actors,
    MainThreadCallbacks callbacks) {

  @DefaultSubcommand
  public CommandResult execute(
      @NonNull CommandActor actor, @Suggestions("players") Optional<String> requesterName) {
    var sender = actor.unwrap(Player.class);

    if (requesterName.isEmpty()) {
      MenuOpenings.open(this.menus, sender, TpaPendingMenu.ID, actor);
      return CommandResult.success();
    }

    var found = this.resolver.resolve(sender, requesterName.get(), actor);
    if (found.isEmpty()) {
      return CommandResult.success();
    }

    var request = found.get();
    var claim = this.service.tryAccept(request);

    this.acceptHandler.handleClaim(claim, request, actor);

    if (claim != AcceptOutcome.ACCEPTED) {
      return CommandResult.success();
    }

    var pending = this.service.dispatchTeleport(request);
    this.callbacks.hop(
        pending,
        success -> this.teleportNotifier.notifyOutcome(success, actor),
        "tpa accept command");

    return CommandResult.success();
  }
}
