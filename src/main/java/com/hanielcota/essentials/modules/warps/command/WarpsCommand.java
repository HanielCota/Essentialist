package com.hanielcota.essentials.modules.warps.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.modules.warps.service.WarpService;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PlayerOnly;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("warps")
@EssentialsCommand
@Permission("essentials.warp.list")
@Cooldown(duration = "3s")
@Description("Lista as warps que você pode usar, clicáveis para teleporte.")
@Syntax("/warps")
public record WarpsCommand(@NonNull WarpService service, @NonNull WarpsListNotifier notifier) {

  @DefaultSubcommand
  @PlayerOnly
  public CommandResult execute(@NonNull CommandActor actor) {
    var sender = actor.unwrap(Player.class);
    var warps = this.service.visibleTo(sender);

    if (warps.isEmpty()) {
      this.notifier.sendEmpty(actor);
      return CommandResult.invalidUsage();
    }

    this.notifier.sendList(sender, warps);
    return CommandResult.success();
  }
}
