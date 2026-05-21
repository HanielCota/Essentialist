package com.hanielcota.essentials.modules.clear.command;

import com.hanielcota.essentials.command.ActorMessages;
import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.clear.config.ClearConfig;
import com.hanielcota.essentials.modules.clear.service.ClearService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Confirm;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PermissionForOther;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import org.bukkit.entity.Player;

@Command(value = "limpar", aliases = "clear")
@EssentialsCommand
@Permission("essentials.clear")
@PermissionForOther("essentials.clear.others")
@Cooldown(duration = "5s")
@Confirm(duration = "3s")
@Description("Limpa o inventário do jogador.")
@Syntax("/limpar [jogador]")
public record ClearCommand(
    ConfigHandle<ClearConfig> config, ClearService service, ActorMessages messages) {

  @DefaultSubcommand
  public void execute(CommandActor sender, @TargetOrSelf Player subject) {
    int removed = service.clear(subject);
    var snap = config.value();
    String name = subject.getName();

    if (removed == 0) {
      sender.sendError(snap.whenEmpty().format(true, name));
      return;
    }

    var pair = snap.whenCleared();
    messages.notifyTarget(
        sender,
        subject,
        pair.format(true, name).replace("{count}", Integer.toString(removed)),
        pair.self().replace("{count}", Integer.toString(removed)));
  }
}
