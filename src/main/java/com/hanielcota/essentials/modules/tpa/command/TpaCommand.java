package com.hanielcota.essentials.modules.tpa.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.menu.TpaHelpMenu;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequestType;
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
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("tpa")
@EssentialsCommand
@Permission("essentials.tpa")
@Cooldown(duration = "5s")
@Description("Abre o menu de ajuda do TPA, ou pede teleporte até outro jogador.")
@Syntax("/tpa [jogador]")
public record TpaCommand(
    ConfigHandle<TpaConfig> config,
    TeleportRequestService service,
    PlayerProvider players,
    MenuService menus) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor, @DefaultValue("") @NonNull String targetName) {
    var sender = actor.unwrap(Player.class);

    if (targetName.isEmpty()) {
      MenuOpenings.open(this.menus, sender, TpaHelpMenu.ID, actor);
      return;
    }

    var snap = this.config.value();
    var messages = snap.messages();

    var resolved = this.players.online(targetName);
    if (resolved.isEmpty()) {
      var notFoundTemplate = messages.playerNotFound();
      var notFoundMsg = notFoundTemplate.replace("{player}", targetName);
      actor.sendError(notFoundMsg);
      return;
    }

    var target = resolved.get();
    var confirmationTemplate = messages.requestSent();
    var type = TeleportRequestType.TPA;

    TpaRequests.send(this.service, messages, actor, target, type, confirmationTemplate);
  }
}
