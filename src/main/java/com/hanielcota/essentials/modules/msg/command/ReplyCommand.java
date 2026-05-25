package com.hanielcota.essentials.modules.msg.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.msg.config.MsgConfig;
import com.hanielcota.essentials.modules.msg.service.MsgDispatcher;
import com.hanielcota.essentials.modules.msg.service.MsgService;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.GreedyString;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.UUID;
import java.util.function.BiPredicate;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command(value = "r", aliases = "reply")
@Permission("essentials.msg.reply")
@Cooldown(duration = "1s")
@Description("Responde ao último jogador que enviou ou recebeu sua mensagem.")
@Syntax("/r <mensagem>")
public record ReplyCommand(
    ConfigHandle<MsgConfig> config,
    MsgService partners,
    MsgDispatcher dispatcher,
    PlayerProvider players,
    BiPredicate<Player, Player> visibilityFilter) {

  @DefaultSubcommand
  @PlayerOnly
  public void execute(
      @NonNull CommandActor sender, @GreedyString @Arg("mensagem") String mensagem) {
    var snap = this.config.value();
    var body = mensagem.strip();
    var from = sender.unwrap(Player.class);
    var fromId = from.getUniqueId();
    var partnerId = this.partners.lastPartner(fromId).orElse(null);

    if (body.isEmpty()) {
      var emptyMsg = snap.emptyMessage();
      sender.sendError(emptyMsg);
      return;
    }

    if (partnerId == null) {
      var noPartnerMsg = snap.noReplyPartner();
      sender.sendError(noPartnerMsg);
      return;
    }

    var target = this.players.online(partnerId).orElse(null);

    if (target == null || !this.visibilityFilter.test(from, target)) {
      var partnerName = resolvePartnerName(partnerId, target);
      var offlineMsg = snap.formatReplyPartnerUnavailable(partnerName);
      sender.sendError(offlineMsg);
      return;
    }

    this.dispatcher.send(from, target, body);
  }

  private String resolvePartnerName(@NonNull UUID id, Player onlinePartner) {
    if (onlinePartner != null) {
      return onlinePartner.getName();
    }

    var offline = this.players.offline(id);
    var stored = offline.getName();

    return stored != null ? stored : id.toString();
  }
}
