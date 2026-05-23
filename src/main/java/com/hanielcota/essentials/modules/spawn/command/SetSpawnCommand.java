package com.hanielcota.essentials.modules.spawn.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.spawn.config.SpawnConfig;
import com.hanielcota.essentials.modules.spawn.service.SpawnLocation;
import com.hanielcota.essentials.modules.spawn.service.SpawnService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("setspawn")
@EssentialsCommand
@Permission("essentials.spawn.set")
@Cooldown(duration = "1s")
@Description("Define o spawn do servidor na sua localização atual.")
@Syntax("/setspawn")
public record SetSpawnCommand(ConfigHandle<SpawnConfig> config, SpawnService service) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor) {
    Player sender = actor.unwrap(Player.class);
    this.service.set(SpawnLocation.of(sender.getLocation()));
    actor.sendSuccess(this.config.value().messages().spawnSet());
  }
}
