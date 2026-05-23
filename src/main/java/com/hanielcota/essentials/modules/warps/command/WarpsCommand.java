package com.hanielcota.essentials.modules.warps.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.warps.config.WarpsConfig;
import com.hanielcota.essentials.modules.warps.service.Warp;
import com.hanielcota.essentials.modules.warps.service.WarpService;
import com.hanielcota.essentials.util.ClickableMessage;
import com.hanielcota.essentials.util.Numbers;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("warps")
@EssentialsCommand
@Permission("essentials.warp.list")
@Cooldown(duration = "3s")
@Description("Lista as warps que você pode usar, clicáveis para teleporte.")
@Syntax("/warps")
public record WarpsCommand(ConfigHandle<WarpsConfig> config, WarpService service) {

  public WarpsCommand(@NonNull ConfigHandle<WarpsConfig> config, @NonNull WarpService service) {
    this.config = config;
    this.service = service;
  }

  private static String renderEntry(@NonNull Warp warp, @NonNull String template) {
    var compactX = Numbers.compact(warp.x());
    var compactY = Numbers.compact(warp.y());
    var compactZ = Numbers.compact(warp.z());

    return template
        .replace("{name}", warp.name())
        .replace("{world}", warp.world())
        .replace("{x}", compactX)
        .replace("{y}", compactY)
        .replace("{z}", compactZ);
  }

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor) {
    var sender = actor.unwrap(Player.class);
    var messages = this.config.value().messages();

    var warps = this.service.listVisibleTo(sender);
    if (warps.isEmpty()) {
      actor.sendError(messages.noWarps());
      return;
    }

    var warpsCountStr = Integer.toString(warps.size());
    var header = messages.listHeader().replace("{count}", warpsCountStr);

    var message = ClickableMessage.create();
    message.append(header);

    for (var warp : warps) {
      var warpName = warp.name();
      var entryText = renderEntry(warp, messages.listEntry());

      var command = "/warp " + warpName;
      var hoverText = messages.listEntryHover().replace("{name}", warpName);

      message.newline().append(entryText, slot -> slot.runCommand(command).hover(hoverText));
    }

    message.send(sender);
  }
}
