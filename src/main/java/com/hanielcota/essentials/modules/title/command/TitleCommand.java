package com.hanielcota.essentials.modules.title.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.title.config.TitleConfig;
import com.hanielcota.essentials.modules.title.service.TitleRequest;
import com.hanielcota.essentials.modules.title.service.TitleService;
import io.github.hanielcota.commandframework.annotation.*;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("title")
@Permission("essentials.title")
@Description("Envia um título na tela do jogador.")
@Syntax("/title [jogador] \"título\" [\"subtítulo\"]")
public record TitleCommand(ConfigHandle<TitleConfig> config, TitleService service) {

  @DefaultSubcommand
  @Cooldown(duration = "3s")
  public void execute(@NonNull CommandActor sender, @GreedyString @Arg("texto") String texto) {
    var snap = this.config.value();
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

    this.service.send(target, request.message());

    var sentMsg = snap.whenSent().forSender(toSelf, target.getName());
    sender.sendSuccess(sentMsg);
  }

  @Subcommand("broadcast")
  @Alias("bc")
  @Permission("essentials.title.broadcast")
  @Description("Envia um título para todos os jogadores online.")
  @Syntax("/title broadcast \"título\" [\"subtítulo\"]")
  public void broadcast(@NonNull CommandActor sender, @GreedyString @Arg("texto") String texto) {
    var snap = this.config.value();
    var message = texto.strip();

    if (message.isBlank()) {
      sender.sendError(snap.usage());
      return;
    }

    var count = this.service.broadcast(message);
    var broadcastedMsg = snap.formatBroadcasted(count);
    sender.sendSuccess(broadcastedMsg);
  }
}
