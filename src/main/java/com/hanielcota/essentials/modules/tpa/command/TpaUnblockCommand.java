package com.hanielcota.essentials.modules.tpa.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.service.TpaBlockService;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("tpaunblock")
@EssentialsCommand
@Permission("essentials.tpa")
@Cooldown(duration = "1s")
@Description("Remove o bloqueio de TPA de um jogador.")
@Syntax("/tpaunblock <jogador>")
public record TpaUnblockCommand(
    ConfigHandle<TpaConfig> config, TpaBlockService blocks, PlayerProvider players) {

  @DefaultSubcommand
  public CommandResult execute(@NonNull CommandActor actor, @NonNull String targetName) {
    var sender = actor.unwrap(Player.class);
    var messages = this.config.value().messages();
    var resolved = this.players.offlineByName(targetName);

    if (resolved.isEmpty()) {
      return CommandResult.invalidUsage(
          actor, messages.playerNotFound().replace("{player}", targetName));
    }

    var target = resolved.get();
    var targetId = target.getUniqueId();
    var targetDisplayName = target.getName() != null ? target.getName() : targetName;

    this.blocks.unblock(sender.getUniqueId(), targetId);

    actor.sendSuccess(messages.unblockedPlayer().replace("{player}", targetDisplayName));

    return CommandResult.success();
  }
}
