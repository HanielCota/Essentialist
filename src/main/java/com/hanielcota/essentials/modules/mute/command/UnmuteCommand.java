package com.hanielcota.essentials.modules.mute.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.mute.config.MuteConfig;
import com.hanielcota.essentials.modules.mute.service.MuteService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.OnlinePlayer;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("unmute")
@Permission("essentials.unmute")
@Description("Remove o silêncio de um jogador.")
@Syntax("/unmute <jogador>")
public record UnmuteCommand(
    ConfigHandle<MuteConfig> config, MuteService service, PaperCommandFramework framework) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor sender, @OnlinePlayer @NonNull Player target) {
    var snap = this.config.value();
    var name = target.getName();
    var targetId = target.getUniqueId();

    var removed = this.service.unmute(targetId);
    if (!removed) {
      var notMutedMsg = snap.formatNotMuted(name);
      sender.sendError(notMutedMsg);
      return;
    }

    var senderMsg = snap.formatUnmutedSender(name);
    var targetMsg = snap.unmutedTarget();
    var targetActor = this.framework.actorOf(target);

    sender.sendMessage(senderMsg);
    targetActor.sendMessage(targetMsg);
  }
}
