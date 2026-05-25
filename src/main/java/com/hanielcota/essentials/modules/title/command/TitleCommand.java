package com.hanielcota.essentials.modules.title.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.title.config.TitleConfig;
import com.hanielcota.essentials.modules.title.service.TitleRequest;
import com.hanielcota.essentials.modules.title.service.TitleService;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.annotation.*;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("title")
@Permission("essentials.title")
@Description("Envia um título na tela do jogador.")
@Syntax("/title [jogador] \"título\" [\"subtítulo\"]")
public record TitleCommand(
    ConfigHandle<TitleConfig> config, TitleService service, PlayerProvider players) {

  @DefaultSubcommand
  @Cooldown(duration = "3s")
  public void execute(@NonNull CommandActor sender, @GreedyString @Arg("texto") String texto) {
    var snap = this.config.value();
    var self = sender.isPlayer() ? sender.unwrap(Player.class) : null;
    var input = texto.strip();
    var request = TitleRequest.from(self, input, this.players);

    var target = request.target();
    var message = request.message();

    if (target == null || message.isBlank()) {
      var usageMsg = snap.usage();
      sender.sendError(usageMsg);
      return;
    }

    var toSelf = target.equals(self);

    if (!toSelf && !sender.hasPermission("essentials.title.others")) {
      var noPermissionMsg = snap.noPermissionOther();
      sender.sendError(noPermissionMsg);
      return;
    }

    this.service.send(target, message);

    var targetName = target.getName();
    var messages = snap.whenSent();
    var sentMsg = messages.forSender(toSelf, targetName);

    sender.sendSuccess(sentMsg);
  }

  @Subcommand("broadcast")
  @Permission("essentials.title.broadcast")
  @Description("Envia um título para todos os jogadores online.")
  @Syntax("/title broadcast \"título\" [\"subtítulo\"]")
  public void broadcast(@NonNull CommandActor sender, @GreedyString @Arg("texto") String texto) {
    var snap = this.config.value();
    var message = texto.strip();

    if (message.isBlank()) {
      var usageMsg = snap.usage();
      sender.sendError(usageMsg);
      return;
    }

    var count = this.service.broadcast(message);
    var broadcastedMsg = snap.formatBroadcasted(count);

    sender.sendSuccess(broadcastedMsg);
  }
}
