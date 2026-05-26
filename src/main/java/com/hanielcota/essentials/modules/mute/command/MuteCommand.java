package com.hanielcota.essentials.modules.mute.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.modules.mute.domain.MuteOutcome;
import com.hanielcota.essentials.modules.mute.service.MuteService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.DefaultValue;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.GreedyString;
import io.github.hanielcota.commandframework.annotation.OnlinePlayer;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("mute")
@Permission("essentials.mute")
@Description("Silencia o chat de um jogador.")
@Syntax("/mute <jogador> [duração]")
public record MuteCommand(MuteService service, MuteNotifier notifier) {

  @DefaultSubcommand
  public void execute(
      @NonNull CommandActor sender,
      @OnlinePlayer @NonNull Player target,
      @DefaultValue("") @GreedyString @Arg("duracao") String duracao) {
    if (Senders.isSelf(sender, target)) {
      this.notifier.sendCannotMuteSelf(sender);
      return;
    }

    var outcome = this.service.mute(target, duracao);
    switch (outcome) {
      case MuteOutcome.Exempt exempt -> this.notifier.sendExempt(sender, exempt.targetName());
      case MuteOutcome.InvalidDuration invalid ->
          this.notifier.sendInvalidDuration(sender, invalid.raw());
      case MuteOutcome.Muted muted -> this.notifier.sendMuted(sender, target, muted.mute());
    }
  }
}
