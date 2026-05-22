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
import java.util.Objects;
import org.bukkit.entity.Player;

@Command("actionbar")
@Permission("essentials.actionbar")
@Cooldown(duration = "3s")
@Description("Envia uma mensagem na action bar.")
@Syntax("/actionbar <mensagem> | /actionbar broadcast <mensagem>")
public record ActionBarCommand(ConfigHandle<ActionBarConfig> config, ActionBarService service) {

  @DefaultSubcommand
  @PlayerOnly
  public void execute(CommandActor sender, @GreedyString @Arg("mensagem") String mensagem) {
    Objects.requireNonNull(sender, "sender");
    Objects.requireNonNull(mensagem, "mensagem");

    var snap = config.value();

    String message = mensagem.strip();
    if (message.isBlank()) {
      sender.sendError(snap.usage());
      return;
    }

    service.send(sender.unwrap(Player.class), message);
    sender.sendSuccess(snap.sent());
  }

  @Subcommand("broadcast")
  @Permission("essentials.actionbar.broadcast")
  @Description("Envia uma action bar para todos os jogadores online.")
  @Syntax("/actionbar broadcast <mensagem>")
  public void broadcast(CommandActor sender, @GreedyString @Arg("mensagem") String mensagem) {
    Objects.requireNonNull(sender, "sender");
    Objects.requireNonNull(mensagem, "mensagem");

    var snap = config.value();
    String message = mensagem.strip();
    if (message.isBlank()) {
      sender.sendError(snap.usage());
      return;
    }

    int count = service.broadcast(message);
    sender.sendSuccess(snap.formatBroadcasted(count));
  }
}
