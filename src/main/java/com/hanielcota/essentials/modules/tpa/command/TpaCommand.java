package com.hanielcota.essentials.modules.tpa.command;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.domain.TpaTargetSelection;
import com.hanielcota.essentials.modules.tpa.menu.TpaHelpMenu;
import com.hanielcota.essentials.modules.tpa.menu.TpaTargetActionMenu;
import com.hanielcota.essentials.modules.tpa.service.TpaTargetSelections;
import com.hanielcota.essentials.paper.PlayerProvider;
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

@Command("tpa")
@EssentialsCommand
@Permission("essentials.tpa")
@PlayerOnly
@Cooldown(duration = "5s")
@Description("Abre o menu de ações de TPA com o jogador (ou o hub se nenhum nick for passado).")
@Syntax("/tpa [jogador]")
public record TpaCommand(
    ConfigHandle<TpaConfig> config,
    PlayerProvider players,
    MenuService menus,
    TpaTargetSelections selections) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor, Optional<String> targetName) {
    var sender = actor.unwrap(Player.class);

    if (targetName.isEmpty()) {
      MenuOpenings.open(this.menus, sender, TpaHelpMenu.ID, actor);
      return CommandResult.success();
    }

    var snap = this.config.value();
    var messages = snap.messages();
    var name = targetName.get();

    var resolved = this.players.online(name);
    if (resolved.isEmpty()) {
      var notFoundTemplate = messages.playerNotFound();
      var notFoundMsg = notFoundTemplate.replace("{player}", name);
      return CommandResult.invalidUsage(actor, notFoundMsg);
    }

    var target = resolved.get();
    if (sender.getUniqueId().equals(target.getUniqueId())) {
      return CommandResult.invalidUsage(actor, messages.selfTarget());
    }

    var selection =
        new TpaTargetSelection(target.getUniqueId(), target.getName(), TeleportRequestType.TPA);
    this.selections.select(sender.getUniqueId(), selection);

    MenuOpenings.open(this.menus, sender, TpaTargetActionMenu.ID, actor);
    return CommandResult.success();
  }
}
