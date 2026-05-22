package com.hanielcota.essentials.modules.title.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.title.config.TitleConfig;
import com.hanielcota.essentials.modules.title.service.TitleService;
import io.github.hanielcota.commandframework.annotation.Alias;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.GreedyString;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import org.bukkit.entity.Player;

@Command("title")
@Permission("essentials.title")
@Description("Envia um título na tela do jogador.")
@Syntax("/title [jogador] <título> | <subtítulo>")
public record TitleCommand(ConfigHandle<TitleConfig> config, TitleService service) {

  @DefaultSubcommand
  @Cooldown(duration = "3s")
  public void execute(CommandActor sender, @GreedyString @Arg("texto") String texto) {
    var snap = config.value();
    Player self = sender.isPlayer() ? sender.unwrap(Player.class) : null;
    TitleRequest request = TitleRequest.from(self, texto.strip());

    if (request.target() == null || request.message().isBlank()) {
      sender.sendError(snap.usage());
      return;
    }

    Player target = request.target();
    boolean toSelf = target.equals(self);
    if (!toSelf && !sender.hasPermission("essentials.title.others")) {
      sender.sendError(snap.noPermissionOther());
      return;
    }

    service.send(target, request.message());
    sender.sendSuccess(toSelf ? snap.sent() : snap.formatSentOther(target.getName()));
  }

  @Subcommand("broadcast")
  @Alias("bc")
  @Permission("essentials.title.broadcast")
  @Description("Envia um título para todos os jogadores online.")
  @Syntax("/title broadcast <título> | <subtítulo>")
  public void broadcast(CommandActor sender, @GreedyString @Arg("texto") String texto) {
    var snap = config.value();
    String message = texto.strip();
    if (message.isBlank()) {
      sender.sendError(snap.usage());
      return;
    }

    int count = service.broadcast(message);
    sender.sendSuccess(snap.formatBroadcasted(count));
  }
}
