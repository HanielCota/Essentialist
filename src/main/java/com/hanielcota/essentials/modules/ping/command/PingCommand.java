package com.hanielcota.essentials.modules.ping.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.ping.config.PingConfig;
import com.hanielcota.essentials.modules.ping.service.PingService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PermissionForOther;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import org.bukkit.entity.Player;

@Command("ping")
@EssentialsCommand
@Permission("essentials.ping")
@PermissionForOther("essentials.ping.others")
@Cooldown(duration = "3s")
@Description("Mostra o ping do jogador.")
@Syntax("/ping [jogador]")
public record PingCommand(ConfigHandle<PingConfig> config, PingService service) {

  @DefaultSubcommand
  public void execute(CommandActor sender, @TargetOrSelf Player subject) {
    String name = subject.getName();
    boolean self = sender.uniqueId().equals(subject.getUniqueId().toString());

    String coloredPing = service.format(subject.getPing());
    String message = config.value().message().forSender(self, name).replace("{ping}", coloredPing);

    sender.sendMessage(message);
  }
}
