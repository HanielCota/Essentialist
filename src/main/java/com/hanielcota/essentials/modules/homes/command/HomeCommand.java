package com.hanielcota.essentials.modules.homes.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.service.HomeNameResolver;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.modules.homes.service.HomeTeleporter;
import com.hanielcota.essentials.modules.homes.service.MissingHomeMessageResolver;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("home")
@EssentialsCommand
@Permission("essentials.home.use")
@Cooldown(duration = "2s")
@Description("Teleporta para uma home (ou \"home\" se ausente).")
@Syntax("/home [nome]")
public record HomeCommand(
    ConfigHandle<HomesConfig> config,
    HomeService service,
    HomeTeleporter teleporter,
    HomeNameResolver nameResolver,
    MissingHomeMessageResolver missingResolver) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor, @Arg("nome") Optional<String> rawName) {
    var sender = actor.unwrap(Player.class);
    var snap = this.config.value();
    var messages = snap.messages();
    var name = this.nameResolver.resolve(rawName.orElse(""));

    if (name == null) {
      var invalidNameMsg = messages.invalidName();
      return CommandResult.invalidUsage(actor, invalidNameMsg);
    }

    var uuid = sender.getUniqueId();
    var home = this.service.findHome(uuid, name);

    if (home.isEmpty()) {
      var missingMsg = this.missingResolver.resolve(uuid, name);
      return CommandResult.invalidUsage(actor, missingMsg);
    }

    var target = home.get();

    this.teleporter.teleport(sender, target, actor);
    return CommandResult.success();
  }
}
