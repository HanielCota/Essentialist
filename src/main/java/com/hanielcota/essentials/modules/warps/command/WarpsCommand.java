package com.hanielcota.essentials.modules.warps.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.warps.config.WarpsConfig;
import com.hanielcota.essentials.modules.warps.domain.Warp;
import com.hanielcota.essentials.modules.warps.service.WarpService;
import com.hanielcota.essentials.util.ClickableMessage;
import com.hanielcota.essentials.util.ClickableMessageSegment;
import com.hanielcota.essentials.util.Numbers;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.function.Consumer;
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
    var warpName = warp.name();
    var worldName = warp.world();

    var compactX = Numbers.compact(warp.x());
    var compactY = Numbers.compact(warp.y());
    var compactZ = Numbers.compact(warp.z());

    var withName = template.replace("{name}", warpName);
    var withWorld = withName.replace("{world}", worldName);
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
      var noWarpsMsg = messages.noWarps();
      actor.sendError(noWarpsMsg);
      return;
    }

    var warpsCount = warps.size();
    var warpsCountStr = Integer.toString(warpsCount);
    var headerTemplate = messages.listHeader();
    var headerMsg = headerTemplate.replace("{count}", warpsCountStr);

    var entryTemplate = messages.listEntry();
    var hoverTemplate = messages.listEntryHover();

    var message = ClickableMessage.create();
    message.append(headerMsg);

    for (var warp : warps) {
      appendEntry(message, warp, entryTemplate, hoverTemplate);
    }

    message.send(sender);
  }

  private static void appendEntry(
      @NonNull ClickableMessage message,
      @NonNull Warp warp,
      @NonNull String entryTemplate,
      @NonNull String hoverTemplate) {
    var warpName = warp.name();
    var entryMsg = renderEntry(warp, entryTemplate);

    var command = "/warp " + warpName;
    var hoverMsg = hoverTemplate.replace("{name}", warpName);

    Consumer<ClickableMessageSegment> decorate =
        slot -> {
          slot.runCommand(command);
          slot.hover(hoverMsg);
        };

    message.newline();
    message.append(entryMsg, decorate);
  }
}
