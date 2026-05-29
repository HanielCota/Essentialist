package com.hanielcota.essentials.modules.more.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.more.config.MoreConfig;
import com.hanielcota.essentials.modules.more.service.MoreService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("more")
@EssentialsCommand
@Permission("essentials.more")
@Description("Enche o item na mão até o tamanho máximo da pilha.")
@Syntax("/more")
public record MoreCommand(ConfigHandle<MoreConfig> config, MoreService service) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor) {
    var player = actor.unwrap(Player.class);
    var snap = this.config.value();

    var result = this.service.fill(player);

    return switch (result) {
      case FILLED -> {
        actor.sendSuccess(snap.filled());
        yield CommandResult.success();
      }
      case EMPTY_HAND -> CommandResult.invalidUsage(snap.emptyHand());
      case ALREADY_FULL -> CommandResult.invalidUsage(snap.alreadyFull());
    };
  }
}
