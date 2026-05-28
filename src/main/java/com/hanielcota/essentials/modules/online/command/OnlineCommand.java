package com.hanielcota.essentials.modules.online.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.online.config.OnlineConfig;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import java.util.function.ToIntFunction;
import lombok.NonNull;

@Command("online")
@Permission("essentials.online")
@Description("Mostra quantos jogadores estão online.")
@Syntax("/online")
public record OnlineCommand(
    ConfigHandle<OnlineConfig> config,
    PlayerProvider players,
    ToIntFunction<CommandActor> visibleCount) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor) {
    var snap = this.config.value();
    var onlineCount = this.visibleCount.applyAsInt(actor);
    var maxPlayers = this.players.maxPlayers();

    var message = snap.format(onlineCount, maxPlayers);

    actor.sendMessage(message);

    return CommandResult.success();
  }
}
