package com.hanielcota.essentials.modules.spawnmob.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.spawnmob.config.SpawnMobConfig;
import com.hanielcota.essentials.modules.spawnmob.service.SpawnMobService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.DefaultValue;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Min;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import java.util.Locale;
import lombok.NonNull;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@Command("spawnmob")
@EssentialsCommand
@PlayerOnly
@Permission("essentials.spawnmob")
@Description("Invoca mobs na sua localização.")
@Syntax("/spawnmob <mob> [quantidade]")
public record SpawnMobCommand(ConfigHandle<SpawnMobConfig> config, SpawnMobService service) {

  @DefaultSubcommand
  public CommandResult execute(
      @NonNull CommandActor sender,
      @Arg("mob") EntityType mob,
      @DefaultValue("1") @Min(1) @Arg("quantidade") int amount) {
    var player = sender.unwrap(Player.class);
    var snap = this.config.value();
    var mobName = name(mob);

    if (!isSpawnableMob(mob)) {
      var invalidMsg = snap.formatInvalidMob(mobName);
      return CommandResult.invalidUsage(invalidMsg);
    }

    var max = Math.max(1, snap.maxPerCommand());
    var capped = Math.min(amount, max);

    this.service.spawn(player, mob, capped);

    var spawnedMsg = snap.formatSpawned(capped, mobName);
    sender.sendSuccess(spawnedMsg);
    return CommandResult.success();
  }

  private static boolean isSpawnableMob(@NonNull EntityType type) {
    return type.isSpawnable() && type.isAlive();
  }

  private static String name(@NonNull EntityType type) {
    return type.name().toLowerCase(Locale.ROOT);
  }
}
