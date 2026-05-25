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
import java.util.function.ToIntFunction;
import lombok.NonNull;
import org.bukkit.Bukkit;

@Command("online")
@Permission("essentials.online")
@Cooldown(duration = "3s")
@Description("Mostra quantos jogadores estão online.")
@Syntax("/online")
public record OnlineCommand(
    ConfigHandle<OnlineConfig> config, ToIntFunction<CommandActor> visibleCount) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor) {
    var snap = this.config.value();
    var onlineCount = this.visibleCount.applyAsInt(actor);
    var maxPlayers = Bukkit.getMaxPlayers();

    var message = snap.format(onlineCount, maxPlayers);

    actor.sendMessage(message);
  }
}
