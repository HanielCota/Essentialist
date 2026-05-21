package com.hanielcota.essentials.modules.hat.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.hat.config.HatConfig;
import com.hanielcota.essentials.modules.hat.service.HatService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import org.bukkit.entity.Player;

@Command(value = "chapeu", aliases = "hat")
@EssentialsCommand
@Permission("essentials.hat")
@Cooldown(duration = "3s")
@Description("Equipa o item na mão como chapéu.")
@Syntax("/chapeu")
public record HatCommand(ConfigHandle<HatConfig> config, HatService service) {

  @DefaultSubcommand
  public void execute(CommandActor actor) {
    Player sender = actor.unwrap(Player.class);
    var result = service.equip(sender);
    var snap = config.value();

    if (result == HatService.Result.EMPTY_HAND) {
      actor.sendError(snap.emptyHand());
      return;
    }
    actor.sendSuccess(snap.equipped());
  }
}
