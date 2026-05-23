package com.hanielcota.essentials.modules.spawn.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.spawn.config.SpawnConfig;
import com.hanielcota.essentials.modules.spawn.service.SpawnService;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.util.Placeholders;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import org.bukkit.entity.Player;

@Command("spawn")
@EssentialsCommand
@Permission("essentials.spawn.use")
@Cooldown(duration = "2s")
@Description("Teleporta para o spawn do servidor.")
@Syntax("/spawn")
public record SpawnCommand(
    ConfigHandle<SpawnConfig> config,
    SpawnService service,
    DelayedTeleport delayed,
    PaperCommandFramework framework) {

  @DefaultSubcommand
  public void execute(CommandActor actor) {
    Player sender = actor.unwrap(Player.class);
    var snap = config.value();
    var messages = snap.messages();

    var current = service.current();
    if (current.isEmpty()) {
      actor.sendError(messages.noSpawn());
      return;
    }

    var resolved = current.get().resolve();
    if (resolved.isEmpty()) {
      actor.sendError(messages.worldGone());
      return;
    }

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
                  Placeholders.format(messages.teleporting(), "seconds", Long.toString(seconds)));
            }
          }

          @Override
          public void onSuccess() {
            senderActor.sendSuccess(messages.teleported());
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
