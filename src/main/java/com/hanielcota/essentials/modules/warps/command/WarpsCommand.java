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
import org.bukkit.entity.Player;

@Command("warps")
@EssentialsCommand
@Permission("essentials.warp.list")
@Cooldown(duration = "3s")
@Description("Lista as warps que você pode usar, clicáveis para teleporte.")
@Syntax("/warps")
public record WarpsCommand(ConfigHandle<WarpsConfig> config, WarpService service) {

  @DefaultSubcommand
  public void execute(CommandActor actor) {
    var sender = actor.unwrap(Player.class);
    var messages = config.value().messages();

    var warps = service.listVisibleTo(sender);
    if (warps.isEmpty()) {
      actor.sendError(messages.noWarps());
      return;
    }

    var message = ClickableMessage.create();
    message.append(messages.listHeader().replace("{count}", Integer.toString(warps.size())));

    for (var warp : warps) {
      message
          .newline()
          .append(
              renderEntry(warp, messages.listEntry()),
              slot ->
                  slot.runCommand("/warp " + warp.name())
                      .hover(messages.listEntryHover().replace("{name}", warp.name())));
    }
    message.send(sender);
  }

  private static String renderEntry(Warp warp, String template) {
    return template
        .replace("{name}", warp.name())
        .replace("{world}", warp.world())
        .replace("{x}", Numbers.compact(warp.x()))
        .replace("{y}", Numbers.compact(warp.y()))
        .replace("{z}", Numbers.compact(warp.z()));
  }
}
