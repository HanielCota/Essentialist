package com.hanielcota.essentials.modules.homes.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.config.HomesMessages;
import com.hanielcota.essentials.modules.homes.service.Home;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleportPrompt;
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
import org.bukkit.entity.Player;

@Command("home")
@EssentialsCommand
@Permission("essentials.home.use")
@Cooldown(duration = "2s")
@Description("Teleporta para uma home (ou \"home\" se ausente).")
@Syntax("/home [nome]")
public record HomeCommand(
    ConfigHandle<HomesConfig> config, HomeService service, DelayedTeleport delayed) {

  @DefaultSubcommand
  public void execute(CommandActor actor, @DefaultValue("") @Arg("nome") String rawName) {
    var sender = actor.unwrap(Player.class);
    var snap = config.value();
    var messages = snap.messages();
    var name = rawName.isBlank() ? snap.defaultHomeName() : rawName;

    var home = service.find(sender.getUniqueId(), name);
    if (home.isEmpty()) {
      actor.sendError(missingMessage(messages, sender.getUniqueId(), name));
      return;
    }

    var resolved = home.get().resolve();
    if (resolved.isEmpty()) {
      actor.sendError(messages.worldGone());
      return;
    }

    delayed.schedule(
        sender, resolved.get(), snap.teleportDelay(), prompt(actor, messages, home.get()));
  }

  private DelayedTeleportPrompt prompt(CommandActor actor, HomesMessages messages, Home home) {
    return new DelayedTeleportPrompt(
        actor,
        messages.teleporting().replace("{name}", home.name()),
        messages.teleported().replace("{name}", home.name()),
        messages.cancelled(),
        messages.failed());
  }

  private String missingMessage(HomesMessages messages, UUID owner, String name) {
    if (service.count(owner) == 0) {
      return messages.noHomes();
    }
    return messages.unknownHome().replace("{name}", name);
  }
}
