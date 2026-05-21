package com.hanielcota.essentials.modules.teleport.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.modules.teleport.TeleportContext;
import com.hanielcota.essentials.modules.teleport.service.TeleportService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.OnlinePlayer;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Subcommand;
import io.github.hanielcota.commandframework.annotation.Syntax;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Command("tp")
@EssentialsCommand
@Permission("essentials.tp")
@Cooldown(duration = "3s")
@Description("Teleporta o jogador para outro jogador ou coordenadas.")
@Syntax("/tp <jogador> | /tp move <de> <para> | /tp pos <x> <y> <z>")
public record TeleportCommand(TeleportContext ctx, TeleportService service) {

  @DefaultSubcommand
  @Description("Teleporta você até outro jogador.")
  @Syntax("/tp <jogador>")
  public void toPlayer(Player sender, @OnlinePlayer Player target) {
    var snap = ctx.snapshot();
    if (ctx.isSelf(sender, target)) {
      ctx.error(sender, snap.selfTarget());
      return;
    }
    if (service.teleportTo(sender, target.getLocation())) {
      ctx.notifyTarget(
          sender,
          target,
          snap.formatToPlayer(target.getName()),
          snap.formatTeleportedTo(sender.getName()));
    }
  }

  @Subcommand("move")
  @Permission("essentials.tp.others")
  @Description("Teleporta um jogador até outro.")
  @Syntax("/tp move <de> <para>")
  public void movePlayer(Player sender, @OnlinePlayer Player from, @OnlinePlayer Player to) {
    var snap = ctx.snapshot();
    if (ctx.isSelf(from, to)) {
      ctx.error(sender, snap.selfTarget());
      return;
    }
    if (service.teleportTo(from, to.getLocation())) {
      ctx.success(sender, snap.formatMoveSender(from.getName(), to.getName()));
      if (!ctx.isSelf(from, sender)) {
        ctx.success(from, snap.formatMoveNotify(sender.getName()));
      }
    }
  }

  @Subcommand("pos")
  @Description("Teleporta para coordenadas específicas.")
  @Syntax("/tp pos <x> <y> <z>")
  public void toPos(Player sender, @Arg("x") double x, @Arg("y") double y, @Arg("z") double z) {
    var current = sender.getLocation();
    var destination =
        new Location(sender.getWorld(), x, y, z, current.getYaw(), current.getPitch());
    if (service.teleportTo(sender, destination)) {
      ctx.success(sender, ctx.snapshot().formatToPos(x, y, z));
    }
  }
}
