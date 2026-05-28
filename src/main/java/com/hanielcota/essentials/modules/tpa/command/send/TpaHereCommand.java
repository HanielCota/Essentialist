package com.hanielcota.essentials.modules.tpa.command.send;

import com.github.hanielcota.menuframework.api.MenuService;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.menu.MenuOpenings;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.domain.TpaTargetSelection;
import com.hanielcota.essentials.modules.tpa.menu.target.TpaTargetActionMenu;
import com.hanielcota.essentials.modules.tpa.service.selection.TpaTargetSelections;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.OnlinePlayer;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("tpahere")
@EssentialsCommand
@Permission("essentials.tpa")
@PlayerOnly
@Description("Abre o menu de ações de TPA com o jogador, sugerindo /tpahere.")
@Syntax("/tpahere <jogador>")
public record TpaHereCommand(
    ConfigHandle<TpaConfig> config, MenuService menus, TpaTargetSelections selections) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor, @OnlinePlayer @NonNull Player target) {
    var sender = actor.unwrap(Player.class);
    var senderId = sender.getUniqueId();
    var targetId = target.getUniqueId();

    if (senderId.equals(targetId)) {
      var messages = this.config.value().messages();
      return CommandResult.invalidUsage(messages.selfTarget());
    }

    var selection = new TpaTargetSelection(targetId, target.getName(), TeleportRequestType.TPAHERE);
    this.selections.select(senderId, selection);

    MenuOpenings.open(this.menus, sender, TpaTargetActionMenu.ID, actor);

    return CommandResult.success();
  }
}
