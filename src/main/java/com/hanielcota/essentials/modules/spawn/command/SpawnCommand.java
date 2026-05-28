package com.hanielcota.essentials.modules.spawn.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.spawn.config.SpawnConfig;
import com.hanielcota.essentials.modules.spawn.service.SpawnService;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("spawn")
@EssentialsCommand
@Permission("essentials.spawn.use")
@Description("Teleporta para o spawn do servidor.")
@Syntax("/spawn")
public record SpawnCommand(
    ConfigHandle<SpawnConfig> config, SpawnService service, DelayedTeleport delayed) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor) {
    var snap = this.config.value();
    var messages = snap.messages();

    var current = this.service.current();
    if (current.isEmpty()) {
      var noSpawnMsg = messages.noSpawn();
      return CommandResult.invalidUsage(noSpawnMsg);
    }

    var spawnLocation = current.get();
    var resolved = spawnLocation.resolve();
    if (resolved.isEmpty()) {
      var worldGoneMsg = messages.worldGone();
      return CommandResult.invalidUsage(worldGoneMsg);
    }

    var sender = actor.unwrap(Player.class);
    var destination = resolved.get();
    var delay = snap.teleportDelay();

    var prompt = SpawnPromptFactory.create(actor, messages);

    this.delayed.schedule(sender, destination, delay, prompt);

    return CommandResult.success();
  }
}
