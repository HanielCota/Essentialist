package com.hanielcota.essentials.modules.actionbar.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.actionbar.config.ActionBarConfig;
import com.hanielcota.essentials.modules.actionbar.service.ActionBarService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.GreedyString;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("actionbar")
@Permission("essentials.actionbar")
@Cooldown(duration = "3s")
@Description("Envia uma mensagem na action bar.")
@Syntax("/actionbar <mensagem> | /actionbar broadcast <mensagem>")
public record ActionBarCommand(ConfigHandle<ActionBarConfig> config, ActionBarService service) {

  @DefaultSubcommand
  @PlayerOnly
  public CommandResult execute(
      @NonNull CommandActor sender, @GreedyString @Arg("mensagem") String mensagem) {
    var snap = this.config.value();
    var message = mensagem.strip();

    if (message.isBlank()) {
      var usageMsg = snap.usage();
      return CommandResult.invalidUsage(usageMsg);
    }

    var player = sender.unwrap(Player.class);

    this.service.send(player, message);

    var sentMsg = snap.sent();
    sender.sendSuccess(sentMsg);

    return CommandResult.success();
  }

  @Subcommand("broadcast")
  @Permission("essentials.actionbar.broadcast")
  @Description("Envia uma action bar para todos os jogadores online.")
  @Syntax("/actionbar broadcast <mensagem>")
  public CommandResult broadcast(
      @NonNull CommandActor sender, @GreedyString @Arg("mensagem") String mensagem) {
    var snap = this.config.value();
    var message = mensagem.strip();

    if (message.isBlank()) {
      var usageMsg = snap.usage();
      return CommandResult.invalidUsage(usageMsg);
    }

    var count = this.service.broadcast(message);
    var broadcastedMsg = snap.formatBroadcasted(count);

    sender.sendSuccess(broadcastedMsg);

    return CommandResult.success();
  }
}
