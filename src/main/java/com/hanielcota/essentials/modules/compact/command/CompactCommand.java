package com.hanielcota.essentials.modules.compact.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.compact.config.CompactConfig;
import com.hanielcota.essentials.modules.compact.service.CompactService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command(value = "compactar", aliases = "compact")
@EssentialsCommand
@Permission("essentials.compact")
@PlayerOnly
@Cooldown(duration = "5s")
@Description("Compacta minérios e barras em blocos no seu inventário.")
@Syntax("/compactar")
public record CompactCommand(ConfigHandle<CompactConfig> config, CompactService service) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor) {
    var sender = actor.unwrap(Player.class);
    var blocks = this.service.compact(sender);
    var snap = this.config.value();

    if (blocks == 0) {
      return CommandResult.invalidUsage(snap.nothing());
    }
    var successMsg = snap.formatSuccess(blocks);
    actor.sendSuccess(successMsg);

    return CommandResult.success();
  }
}
