package com.hanielcota.essentials.modules.msg.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.msg.config.MsgConfig;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.GreedyString;
import io.github.hanielcota.commandframework.annotation.OnlinePlayer;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.function.BiPredicate;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command(
    value = "msg",
    aliases = {"tell", "w", "whisper"})
@Permission("essentials.msg")
@Cooldown(duration = "1s")
@Description("Envia uma mensagem privada para outro jogador.")
@Syntax("/msg <jogador> <mensagem>")
public record MsgCommand(
    ConfigHandle<MsgConfig> config,
    MsgDispatcher dispatcher,
    BiPredicate<Player, Player> visibilityFilter) {

  @DefaultSubcommand
  @PlayerOnly
  public void execute(
      @NonNull CommandActor sender,
      @OnlinePlayer @NonNull Player target,
      @GreedyString @Arg("mensagem") String mensagem) {
    var snap = this.config.value();
    var body = mensagem.strip();
    var from = sender.unwrap(Player.class);
    var targetName = target.getName();

    if (body.isEmpty()) {
      var emptyMsg = snap.emptyMessage();
      sender.sendError(emptyMsg);
      return;
    }

    if (Senders.isSelf(sender, target)) {
      var selfMsg = snap.cannotMessageSelf();
      sender.sendError(selfMsg);
      return;
    }

    if (!this.visibilityFilter.test(from, target)) {
      var notFoundMsg = snap.formatTargetUnavailable(targetName);
      sender.sendError(notFoundMsg);
      return;
    }

    this.dispatcher.send(from, target, body);
  }
}
