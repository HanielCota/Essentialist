package com.hanielcota.essentials.modules.tpa.command;

import com.hanielcota.essentials.command.annotation.EssentialsCommand;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.paper.PlayerProvider;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("tpahistory")
@EssentialsCommand
@Permission("essentials.tpa.history")
@Cooldown(duration = "3s")
@Description("Abre o histórico dos últimos pedidos de teleporte enviados.")
@Syntax("/tpahistory [jogador]")
public record TpaHistoryCommand(
    ConfigHandle<TpaConfig> config, PlayerProvider players, TpaHistoryPresenter presenter) {

  private static final String OTHERS_PERMISSION = "essentials.tpa.history.others";

  @DefaultSubcommand
  public CommandResult execute(
      @NonNull CommandActor actor, @Arg("jogador") Optional<String> targetName) {
    var sender = actor.unwrap(Player.class);
    var snap = this.config.value();
    var messages = snap.messages();

    if (targetName.isEmpty()) {
      var senderId = sender.getUniqueId();
      var senderName = sender.getName();
      this.presenter.open(actor, sender, senderId, /* self */ true, senderName);

      return CommandResult.success();
    }

    if (!actor.hasPermission(OTHERS_PERMISSION)) {
      return CommandResult.invalidUsage(actor, messages.noPermissionOther());
    }

    var resolved = this.players.offlineByName(targetName.orElse(""));
    if (resolved.isEmpty()) {
      var notFoundTemplate = messages.playerNotFound();
      var notFoundMsg = notFoundTemplate.replace("{player}", targetName.orElse(""));

      return CommandResult.invalidUsage(actor, notFoundMsg);
    }

    var target = resolved.get();
    var targetId = target.getUniqueId();

    var resolvedRawName = target.getName();
    var resolvedName = resolvedRawName != null ? resolvedRawName : targetName.orElse("");

    this.presenter.open(actor, sender, targetId, /* self */ false, resolvedName);

    return CommandResult.success();
  }
}
