package com.hanielcota.essentials.modules.tpa.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.service.TeleportRequestService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.OnlinePlayer;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("tpahere")
@EssentialsCommand
@Permission("essentials.tpa")
@Cooldown(duration = "5s")
@Description("Pede para outro jogador se teleportar até você.")
@Syntax("/tpahere <jogador>")
public record TpaHereCommand(
    ConfigHandle<TpaConfig> config,
    TeleportRequestService service,
    TpaSendOrchestrator dispatcher) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor, @OnlinePlayer @NonNull Player target) {
    var snap = this.config.value();
    var messages = snap.messages();

    var confirmationTemplate = messages.requestSentHere();
    var type = TeleportRequestType.TPAHERE;

    this.dispatcher.send(actor, target, type, confirmationTemplate);
  }
}
