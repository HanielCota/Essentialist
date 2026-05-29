package com.hanielcota.essentials.modules.spawn.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.spawn.config.SpawnConfig;
import com.hanielcota.essentials.modules.spawn.domain.SpawnLocation;
import com.hanielcota.essentials.modules.spawn.service.SpawnService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("setspawn")
@EssentialsCommand
@PlayerOnly
@Permission("essentials.spawn.set")
@Description("Define o spawn do servidor na sua localização atual.")
@Syntax("/setspawn")
public record SetSpawnCommand(ConfigHandle<SpawnConfig> config, SpawnService service) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor) {
    var sender = actor.unwrap(Player.class);
    var senderLocation = sender.getLocation();
    var spawn = SpawnLocation.of(senderLocation);

    this.service.set(spawn);

    var snap = this.config.value();
    var messages = snap.messages();
    var spawnSetMsg = messages.spawnSet();

    actor.sendSuccess(spawnSetMsg);

    return CommandResult.success();
  }
}
