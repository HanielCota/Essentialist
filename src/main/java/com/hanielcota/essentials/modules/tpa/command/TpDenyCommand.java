package com.hanielcota.essentials.modules.tpa.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.tpa.command.send.TpaIncomingResolver;
import com.hanielcota.essentials.modules.tpa.command.send.TpaRequestReplyNotifier;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.menu.pending.TpaPendingMenu;
import com.hanielcota.essentials.modules.tpa.service.request.TeleportRequestService;
import com.hanielcota.essentials.paper.ActorFactory;
import io.github.hanielcota.commandframework.annotation.Command;
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

@Command("tpdeny")
@EssentialsCommand
@Permission("essentials.tpa")
@PlayerOnly
@Description("Recusa um pedido de teleporte (ou abre o menu se nenhum nick for passado).")
@Syntax("/tpdeny [jogador]")
public record TpDenyCommand(
    ConfigHandle<TpaConfig> config,
    MenuService menus,
    TeleportRequestService service,
    TpaRequestReplyNotifier replyNotifier,
    TpaIncomingResolver resolver,
    ActorFactory actors) {

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
    var denied = this.service.deny(request);

    if (!denied) {
      var messages = this.config.value().messages();
      actor.sendError(messages.noIncoming());
      return CommandResult.success();
    }

    var messages = this.config.value().messages();
    var requesterNameStr = request.requester().name();
    var deniedSelf = messages.deniedSelf().replace("{player}", requesterNameStr);
    actor.sendSuccess(deniedSelf);

    var deniedTemplate = messages.denied();
    this.replyNotifier.notifyDenied(request, deniedTemplate);

    return CommandResult.success();
  }
}
