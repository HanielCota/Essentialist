package com.hanielcota.essentials.modules.warps.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.warps.config.WarpsConfig;
import com.hanielcota.essentials.modules.warps.domain.Warp;
import com.hanielcota.essentials.util.ClickableMessage;
import com.hanielcota.essentials.util.ClickableMessageSegment;
import com.hanielcota.essentials.util.Numbers;
import io.github.hanielcota.commandframework.core.CommandActor;
import java.util.List;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Renders the {@code /warps} list as a clickable message and dispatches it. Keeps the per-entry
 * formatting and the {@code runCommand}/hover decoration out of the command class so the command
 * stays a thin dispatcher.
 */
@RequiredArgsConstructor
public final class WarpsListNotifier {

  private final ConfigHandle<WarpsConfig> config;

  public void sendEmpty(@NonNull CommandActor actor) {
    var snap = this.config.value();
    var messages = snap.messages();
    var noWarpsMsg = messages.noWarps();

    actor.sendError(noWarpsMsg);
  }

  public void sendList(@NonNull Player viewer, @NonNull List<Warp> warps) {
    var snap = this.config.value();
    var messages = snap.messages();

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

    message.send(viewer);
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
}
