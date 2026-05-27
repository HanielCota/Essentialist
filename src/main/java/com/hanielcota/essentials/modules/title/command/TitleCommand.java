package com.hanielcota.essentials.modules.title.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.title.config.TitleConfig;
import com.hanielcota.essentials.modules.title.domain.TitleRequest;
import com.hanielcota.essentials.modules.title.service.TitleService;
import com.hanielcota.essentials.paper.PlayerProvider;
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
import io.github.hanielcota.commandframework.core.CommandResult;
import io.github.hanielcota.commandframework.core.CommandStatus;
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
  public CommandResult execute(
      @NonNull CommandActor sender, @GreedyString @Arg("texto") String texto) {
    var snap = this.config.value();
    var self = sender.isPlayer() ? sender.unwrap(Player.class) : null;
    var input = texto.strip();
    var request = TitleRequest.from(self, input, this.players);

    var targetId = request.targetId();
    var targetName = request.targetName();
    var message = request.message();

    if (targetId == null || targetName == null || message.isBlank()) {
      return CommandResult.invalidUsage(snap.usage());
    }

    var toSelf = self != null && targetId.equals(self.getUniqueId());

    if (!toSelf && !sender.hasPermission("essentials.title.others")) {
      return CommandResult.failure(CommandStatus.NO_PERMISSION, snap.noPermissionOther());
    }

    // Re-resolve from the snapshot UUID — the parsed target could have disconnected between the
    // command parse and dispatch on a busy tick.
    var liveTarget = this.players.online(targetId).orElse(null);
    if (liveTarget == null) {
      return CommandResult.invalidUsage(snap.formatTargetOffline(targetName));
    }

    this.service.send(liveTarget, message);

    var messages = snap.whenSent();
    var sentMsg = messages.forSender(toSelf, targetName);

    sender.sendSuccess(sentMsg);
    return CommandResult.success();
  }

  @Subcommand("broadcast")
  @Permission("essentials.title.broadcast")
  @Description("Envia um título para todos os jogadores online.")
  @Syntax("/title broadcast \"título\" [\"subtítulo\"]")
  public CommandResult broadcast(
      @NonNull CommandActor sender, @GreedyString @Arg("texto") String texto) {
    var snap = this.config.value();
    var message = texto.strip();

    if (message.isBlank()) {
      return CommandResult.invalidUsage(snap.usage());
    }

    var count = this.service.broadcast(message);
    var broadcastedMsg = snap.formatBroadcasted(count);

    sender.sendSuccess(broadcastedMsg);
    return CommandResult.success();
  }
}
