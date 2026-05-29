package com.hanielcota.essentials.modules.sudo.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.sudo.config.SudoConfig;
import com.hanielcota.essentials.modules.sudo.service.SudoService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.GreedyString;
import io.github.hanielcota.commandframework.annotation.OnlinePlayer;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("sudo")
@Permission("essentials.sudo")
@Description("Executa um comando como outro jogador online.")
@Syntax("/sudo <jogador> <comando>")
public record SudoCommand(ConfigHandle<SudoConfig> config, SudoService service) {

  @DefaultSubcommand
  public CommandResult execute(
      @NonNull CommandActor sender,
      @OnlinePlayer @NonNull Player target,
      @GreedyString @Arg("comando") String command) {
    var snap = this.config.value();
    var trimmed = command.strip();

    if (trimmed.isEmpty()) {
      var emptyMsg = snap.emptyCommand();
      return CommandResult.invalidUsage(emptyMsg);
    }

    this.service.run(target, trimmed);

    var targetName = target.getName();
    var executedMsg = snap.formatExecuted(targetName, trimmed);

    sender.sendSuccess(executedMsg);
    return CommandResult.success();
  }
}
