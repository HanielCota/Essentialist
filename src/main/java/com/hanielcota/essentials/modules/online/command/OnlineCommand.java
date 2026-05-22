package com.hanielcota.essentials.modules.online.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.online.config.OnlineConfig;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import org.bukkit.Bukkit;

@Command("online")
@Permission("essentials.online")
@Cooldown(duration = "3s")
@Description("Mostra quantos jogadores estão online.")
@Syntax("/online")
public record OnlineCommand(ConfigHandle<OnlineConfig> config) {

  @DefaultSubcommand
  public void execute(CommandActor actor) {
    int count = Bukkit.getOnlinePlayers().size();
    int max = Bukkit.getMaxPlayers();
    actor.sendMessage(config.value().formatMessage(count, max));
  }
}
