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
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command(value = "derreter", aliases = "smelt")
@EssentialsCommand
@Permission("essentials.smelt")
@PlayerOnly
@Cooldown(duration = "5s")
@Description("Derrete minérios no seu inventário.")
@Syntax("/derreter")
public record SmeltCommand(ConfigHandle<SmeltConfig> config, SmeltService service) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor) {
    var sender = actor.unwrap(Player.class);
    var snap = this.config.value();
    var count = this.service.smelt(sender);

    if (count == 0) {
      var nothingMsg = snap.nothing();
      actor.sendError(nothingMsg);
      return;
    }

    var successMsg = snap.formatSuccess(count);
    actor.sendSuccess(successMsg);
  }
}
