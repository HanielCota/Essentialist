package com.hanielcota.essentials.modules.title.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.title.config.TitleConfig;
import com.hanielcota.essentials.modules.title.service.TitleRequest;
import com.hanielcota.essentials.modules.title.service.TitleService;
import io.github.hanielcota.commandframework.annotation.*;
import io.github.hanielcota.commandframework.core.CommandActor;
import org.bukkit.entity.Player;

@Command("title")
@Permission("essentials.title")
@Description("Envia um título na tela do jogador.")
@Syntax("/title [jogador] \"título\" [\"subtítulo\"]")
public record TitleCommand(ConfigHandle<TitleConfig> config, TitleService service) {

  @DefaultSubcommand
  @Cooldown(duration = "3s")
  public void execute(CommandActor sender, @GreedyString @Arg("texto") String texto) {
    var snap = config.value();
    var self = sender.isPlayer() ? sender.unwrap(Player.class) : null;
    var request = TitleRequest.from(self, texto.strip());

    if (request.target() == null || request.message().isBlank()) {
      sender.sendError(snap.usage());
      return;
    }

    var target = request.target();
    var toSelf = target.equals(self);

    if (!toSelf && !sender.hasPermission("essentials.title.others")) {
      sender.sendError(snap.noPermissionOther());
      return;
    }

    service.send(target, request.message());

    sender.sendSuccess(snap.whenSent().forSender(toSelf, target.getName()));
  }

  @Subcommand("broadcast")
  @Alias("bc")
  @Permission("essentials.title.broadcast")
  @Description("Envia um título para todos os jogadores online.")
  @Syntax("/title broadcast \"título\" [\"subtítulo\"]")
  public void broadcast(CommandActor sender, @GreedyString @Arg("texto") String texto) {
    var snap = config.value();
    var message = texto.strip();

    if (message.isBlank()) {
      sender.sendError(snap.usage());
      return;
    }

    var count = service.broadcast(message);
    sender.sendSuccess(snap.formatBroadcasted(count));
  }
}
