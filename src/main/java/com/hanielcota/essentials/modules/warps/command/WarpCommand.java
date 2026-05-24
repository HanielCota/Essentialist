package com.hanielcota.essentials.modules.warps.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.teleport.feedback.DelayedTeleportPrompt;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.modules.warps.config.WarpsConfig;
import com.hanielcota.essentials.modules.warps.config.WarpsMessages;
import com.hanielcota.essentials.modules.warps.domain.Warp;
import com.hanielcota.essentials.modules.warps.service.WarpService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("warp")
@EssentialsCommand
@Permission("essentials.warp")
@Cooldown(duration = "2s")
@Description("Teleporta para uma warp do servidor.")
@Syntax("/warp <nome>")
public record WarpCommand(
    ConfigHandle<WarpsConfig> config, WarpService service, DelayedTeleport delayed) {

  private static final String NAME = "{name}";

  public WarpCommand(
      @NonNull ConfigHandle<WarpsConfig> config,
      @NonNull WarpService service,
      @NonNull DelayedTeleport delayed) {
    this.config = config;
    this.service = service;
    this.delayed = delayed;
  }

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor, @NonNull @Arg("nome") String name) {
    var sender = actor.unwrap(Player.class);
    var snap = this.config.value();
    var messages = snap.messages();

    var warpOpt = this.service.find(name);
    if (warpOpt.isEmpty()) {
      var unknownTemplate = messages.unknownWarp();
      var unknownMsg = unknownTemplate.replace(NAME, name);
      actor.sendError(unknownMsg);
      return;
    }

    var warp = warpOpt.get();
    var resolvedName = warp.name();

    if (!this.service.canUse(sender, resolvedName)) {
      var noPermTemplate = messages.noPermission();
      var noPermMsg = noPermTemplate.replace(NAME, resolvedName);
      actor.sendError(noPermMsg);
      return;
    }

    var locationOpt = warp.resolve();
    if (locationOpt.isEmpty()) {
      actor.sendError(messages.worldGone());
      return;
    }

    var destination = locationOpt.get();
    var delay = snap.teleportDelay();
    var teleportPrompt = prompt(actor, messages, warp);

    this.delayed.schedule(sender, destination, delay, teleportPrompt);
  }

  private DelayedTeleportPrompt prompt(
      @NonNull CommandActor actor, @NonNull WarpsMessages messages, @NonNull Warp warp) {

    var warpName = warp.name();
    var teleportingTemplate = messages.teleporting();
    var teleportingMsg = teleportingTemplate.replace(NAME, warpName);
    var teleportedTemplate = messages.teleported();
    var teleportedMsg = teleportedTemplate.replace(NAME, warpName);
    var cancelledMsg = messages.cancelled();
    var failedMsg = messages.failed();

    return new DelayedTeleportPrompt(actor, teleportingMsg, teleportedMsg, cancelledMsg, failedMsg);
  }
}
