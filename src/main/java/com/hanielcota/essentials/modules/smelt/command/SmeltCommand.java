package com.hanielcota.essentials.modules.smelt.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.smelt.config.SmeltConfig;
import com.hanielcota.essentials.modules.smelt.service.SmeltService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import org.bukkit.entity.Player;

@Command(value = "derreter", aliases = "smelt")
@EssentialsCommand
@Permission("essentials.smelt")
@Cooldown(duration = "5s")
@Description("Derrete minérios no seu inventário.")
@Syntax("/derreter")
public record SmeltCommand(ConfigHandle<SmeltConfig> config, SmeltService service) {

  @DefaultSubcommand
  public void execute(CommandActor actor) {
    Player sender = actor.unwrap(Player.class);
    int count = service.smelt(sender);
    var snap = config.value();

    if (count == 0) {
      actor.sendError(snap.nothing());
      return;
    }
    actor.sendSuccess(snap.formatSuccess(count));
  }
}
