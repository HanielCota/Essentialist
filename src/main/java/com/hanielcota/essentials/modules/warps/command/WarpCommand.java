package com.hanielcota.essentials.modules.warps.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleportPrompt;
import com.hanielcota.essentials.modules.warps.config.WarpsConfig;
import com.hanielcota.essentials.modules.warps.config.WarpsMessages;
import com.hanielcota.essentials.modules.warps.service.Warp;
import com.hanielcota.essentials.modules.warps.service.WarpService;
import com.hanielcota.essentials.util.Placeholders;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import org.bukkit.entity.Player;

@Command("warp")
@EssentialsCommand
@Permission("essentials.warp")
@Cooldown(duration = "2s")
@Description("Teleporta para uma warp do servidor.")
@Syntax("/warp <nome>")
public record WarpCommand(
    ConfigHandle<WarpsConfig> config, WarpService service, DelayedTeleport delayed) {

  @DefaultSubcommand
  public void execute(CommandActor actor, @Arg("nome") String name) {
    Player sender = actor.unwrap(Player.class);
    var snap = config.value();
    var messages = snap.messages();

    var warp = service.find(name);
    if (warp.isEmpty()) {
      actor.sendError(Placeholders.format(messages.unknownWarp(), "name", name));
      return;
    }

    var resolvedName = warp.get().name();
    if (!service.canUse(sender, resolvedName)) {
      actor.sendError(Placeholders.format(messages.noPermission(), "name", resolvedName));
      return;
    }

    var resolved = warp.get().resolve();
    if (resolved.isEmpty()) {
      actor.sendError(messages.worldGone());
      return;
    }

    delayed.schedule(
        sender, resolved.get(), snap.teleportDelay(), prompt(actor, messages, warp.get()));
  }

  private DelayedTeleportPrompt prompt(CommandActor actor, WarpsMessages messages, Warp warp) {
    return new DelayedTeleportPrompt(
        actor,
        Placeholders.format(messages.teleporting(), "name", warp.name()),
        Placeholders.format(messages.teleported(), "name", warp.name()),
        messages.cancelled(),
        messages.failed());
  }
}
