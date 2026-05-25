package com.hanielcota.essentials.modules.mute.command;

import com.hanielcota.essentials.modules.mute.service.MuteService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.OnlinePlayer;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("unmute")
@Permission("essentials.unmute")
@Description("Remove o silêncio de um jogador.")
@Syntax("/unmute <jogador>")
public record UnmuteCommand(MuteService service, MuteNotifier notifier) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor sender, @OnlinePlayer @NonNull Player target) {
    var targetId = target.getUniqueId();
    var removed = this.service.unmute(targetId);

    if (!removed) {
      var name = target.getName();
      this.notifier.sendNotMuted(sender, name);
      return;
    }

    this.notifier.sendUnmuted(sender, target);
  }
}
