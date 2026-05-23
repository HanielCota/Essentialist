package com.hanielcota.essentials.modules.ping.command;

import com.hanielcota.essentials.command.Senders;
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
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("ping")
@Permission("essentials.ping")
@PermissionForOther(".others")
@Cooldown(duration = "3s")
@Description("Mostra o ping do jogador.")
@Syntax("/ping [jogador]")
public record PingCommand(ConfigHandle<PingConfig> config, PingService service) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor sender, @TargetOrSelf @NonNull Player subject) {
    String name = subject.getName();
    boolean self = Senders.isSelf(sender, subject);

    var snap = this.config.value();
    String coloredPing = this.service.format(subject.getPing());
    var message = snap.message().forSender(self, name).replace("{ping}", coloredPing);

    sender.sendMessage(message);
  }
}
