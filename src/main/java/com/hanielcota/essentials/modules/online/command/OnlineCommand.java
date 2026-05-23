package com.hanielcota.essentials.modules.online.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.online.config.OnlineConfig;
import com.hanielcota.essentials.modules.online.service.OnlineService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;

@Command("online")
@Permission("essentials.online")
@Cooldown(duration = "3s")
@Description("Mostra quantos jogadores estão online.")
@Syntax("/online")
public record OnlineCommand(ConfigHandle<OnlineConfig> config, OnlineService service) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor) {
    actor.sendMessage(
        this.config.value().format(this.service.onlineCount(), this.service.maxPlayers()));
  }
}
