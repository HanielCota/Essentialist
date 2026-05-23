package com.hanielcota.essentials.modules.homes.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.homes.config.HomesConfig;
import com.hanielcota.essentials.modules.homes.service.HomeService;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.util.Placeholders;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.DefaultValue;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
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
    DelayedTeleport delayed,
    PaperCommandFramework framework) {

  @DefaultSubcommand
  public void execute(CommandActor actor, @DefaultValue("") @Arg("nome") String rawName) {
    Player sender = actor.unwrap(Player.class);
    var snap = config.value();
    var messages = snap.messages();
    var name = rawName.isBlank() ? snap.defaultHomeName() : rawName;

    var home = service.find(sender.getUniqueId(), name);
    if (home.isEmpty()) {
      var key =
          service.count(sender.getUniqueId()) == 0 ? messages.noHomes() : messages.unknownHome();
      actor.sendError(Placeholders.format(key, "name", name));
      return;
    }

    var resolved = home.get().resolve();
    if (resolved.isEmpty()) {
      actor.sendError(messages.worldGone());
      return;
    }

    var resolvedName = home.get().name();
    var senderActor = framework.actorOf(sender);
    delayed.schedule(
        sender,
        resolved.get(),
        snap.teleportDelay(),
        new DelayedTeleport.Callback() {
          @Override
          public void onScheduled(long seconds) {
            if (seconds > 0) {
              senderActor.sendMessage(
                  Placeholders.format(
                      messages.teleporting(),
                      "name",
                      resolvedName,
                      "seconds",
                      Long.toString(seconds)));
            }
          }

          @Override
          public void onSuccess() {
            senderActor.sendSuccess(
                Placeholders.format(messages.teleported(), "name", resolvedName));
          }

          @Override
          public void onCancelled() {
            senderActor.sendError(messages.cancelled());
          }

          @Override
          public void onFailed() {
            senderActor.sendError(messages.failed());
          }
        });
  }
}
