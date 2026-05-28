package com.hanielcota.essentials.modules.warps.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.teleport.service.DelayedTeleport;
import com.hanielcota.essentials.modules.warps.config.WarpsConfig;
import com.hanielcota.essentials.modules.warps.service.WarpResolver;
import com.hanielcota.essentials.modules.warps.service.WarpService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("warp")
@EssentialsCommand
@Permission("essentials.warp")
@Description("Teleporta para uma warp do servidor.")
@Syntax("/warp <nome>")
public record WarpCommand(
    ConfigHandle<WarpsConfig> config,
    WarpService service,
    WarpResolver resolver,
    DelayedTeleport delayed,
    WarpPromptFactory promptFactory) {

  private static final String NAME = "{name}";

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor, @NonNull @Arg("nome") String name) {
    var sender = actor.unwrap(Player.class);
    var snap = this.config.value();
    var messages = snap.messages();

    var warpOpt = this.service.findWarp(name);
    if (warpOpt.isEmpty()) {
      var unknownTemplate = messages.unknownWarp();
      var unknownMsg = unknownTemplate.replace(NAME, name);
      return CommandResult.invalidUsage(unknownMsg);
    }

    var warp = warpOpt.get();
    var resolvedName = warp.name();

    if (!this.service.canUse(sender, resolvedName)) {
      var noPermTemplate = messages.noPermission();
      var noPermMsg = noPermTemplate.replace(NAME, resolvedName);
      return CommandResult.invalidUsage(noPermMsg);
    }

    var locationOpt = this.resolver.resolve(warp);
    if (locationOpt.isEmpty()) {
      var worldGoneMsg = messages.worldGone();
      return CommandResult.invalidUsage(worldGoneMsg);
    }

    var destination = locationOpt.get();
    var delay = snap.teleportDelay();
    var teleportPrompt = this.promptFactory.create(actor, messages, warp);

    this.delayed.schedule(sender, destination, delay, teleportPrompt);
    return CommandResult.success();
  }
}
