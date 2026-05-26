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
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("tpablock")
@EssentialsCommand
@Permission("essentials.tpa")
@Cooldown(duration = "1s")
@Description("Bloqueia pedidos de TPA de um jogador específico.")
@Syntax("/tpablock <jogador>")
public record TpaBlockCommand(
    ConfigHandle<TpaConfig> config, TpaBlockService blocks, PlayerProvider players) {

  @DefaultSubcommand
  public void execute(@NonNull CommandActor actor, @NonNull String targetName) {
    var sender = actor.unwrap(Player.class);
    var messages = this.config.value().messages();
    var resolved = this.players.offlineByName(targetName);

    if (resolved.isEmpty()) {
      actor.sendError(messages.playerNotFound().replace("{player}", targetName));
      return;
    }

    var target = resolved.get();
    var senderId = sender.getUniqueId();
    var targetId = target.getUniqueId();

    if (senderId.equals(targetId)) {
      actor.sendError(messages.blockSelf());
      return;
    }

    var blockedName = target.getName() != null ? target.getName() : targetName;
    this.blocks.block(senderId, targetId, blockedName);

    actor.sendSuccess(messages.blockedPlayer().replace("{player}", blockedName));
  }
}
