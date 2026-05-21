package com.hanielcota.essentials.modules.teleport.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.modules.teleport.TeleportContext;
import com.hanielcota.essentials.modules.teleport.service.TeleportService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.OnlinePlayer;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import org.bukkit.entity.Player;

@Command("tphere")
@EssentialsCommand
@Permission("essentials.tphere")
@Cooldown(duration = "3s")
@Description("Teleporta um jogador até você.")
@Syntax("/tphere <jogador>")
public record TeleportHereCommand(TeleportContext ctx, TeleportService service) {

  @DefaultSubcommand
  public void execute(Player sender, @OnlinePlayer Player target) {
    var snap = ctx.snapshot();
    if (ctx.isSelf(sender, target)) {
      ctx.error(sender, snap.selfTarget());
      return;
    }
    if (service.teleportTo(target, sender.getLocation())) {
      ctx.notifyTarget(
          sender,
          target,
          snap.formatBroughtPlayer(target.getName()),
          snap.formatBroughtBy(sender.getName()));
    }
  }
}
