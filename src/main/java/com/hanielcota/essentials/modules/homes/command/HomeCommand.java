package com.hanielcota.essentials.modules.homes.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.config.messages.HomesMessages;
import com.hanielcota.essentials.modules.homes.name.HomeNameResolver;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.modules.homes.teleport.HomeTeleporter;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.DefaultValue;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.UUID;
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
    HomeNameResolver nameResolver) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor, @DefaultValue("") @Arg("nome") String rawName) {
    var sender = actor.unwrap(Player.class);
    var messages = this.config.value().messages();
    var name = this.nameResolver.resolve(rawName);
    if (name == null) {
      actor.sendError(messages.invalidName());
      return;
    }

    var home = this.service.find(sender.getUniqueId(), name);
    if (home.isEmpty()) {
      var missingMsg = missingMessage(messages, sender.getUniqueId(), name);
      actor.sendError(missingMsg);
      return;
    }

    this.teleporter.teleport(sender, home.get(), actor);
  }

  private String missingMessage(
      @NonNull HomesMessages messages, @NonNull UUID owner, @NonNull String name) {
    if (this.service.count(owner) == 0) {
      return messages.noHomes();
    }

    var unknownHomeMsg = messages.unknownHome();
    return unknownHomeMsg.replace("{name}", name);
  }
}
