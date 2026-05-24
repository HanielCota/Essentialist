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
public record WarpsCommand(
    @NonNull ConfigHandle<WarpsConfig> config, @NonNull WarpService service) {

  private static String renderEntry(@NonNull Warp warp, @NonNull String template) {
    var compactX = Numbers.compact(warp.x());
    var compactY = Numbers.compact(warp.y());
    var compactZ = Numbers.compact(warp.z());

    var withName = template.replace("{name}", warp.name());
    var withWorld = withName.replace("{world}", warp.world());
    var withX = withWorld.replace("{x}", compactX);
    var withY = withX.replace("{y}", compactY);
    return withY.replace("{z}", compactZ);
  }

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor) {
    var sender = actor.unwrap(Player.class);
    var snap = this.config.value();
    var messages = snap.messages();

    var warps = this.service.listVisibleTo(sender);
    if (warps.isEmpty()) {
      actor.sendError(messages.noWarps());
      return;
    }

    var warpsCountStr = Integer.toString(warps.size());
    var headerTemplate = messages.listHeader();
    var header = headerTemplate.replace("{count}", warpsCountStr);

    var message = ClickableMessage.create();
    message.append(header);

    for (var warp : warps) {
      var warpName = warp.name();
      var entryText = renderEntry(warp, messages.listEntry());

      var command = "/warp " + warpName;
      var hoverTemplate = messages.listEntryHover();
      var hoverText = hoverTemplate.replace("{name}", warpName);

      message.newline();
      message.append(
          entryText,
          slot -> {
            slot.runCommand(command);
            slot.hover(hoverText);
          });
    }

    message.send(sender);
  }
}
