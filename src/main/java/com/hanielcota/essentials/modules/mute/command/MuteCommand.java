package com.hanielcota.essentials.modules.mute.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.modules.mute.domain.MuteOutcome;
import com.hanielcota.essentials.modules.mute.service.MuteService;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.GreedyString;
import io.github.hanielcota.commandframework.annotation.OnlinePlayer;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.core.CommandResult;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("mute")
@Permission("essentials.mute")
@Description("Silencia o chat de um jogador.")
@Syntax("/mute <jogador> [duração]")
public record MuteCommand(MuteService service, MuteNotifier notifier) {

  @DefaultSubcommand
  public CommandResult execute(
      @NonNull CommandActor sender,
      @OnlinePlayer @NonNull Player target,
      @GreedyString @Arg("duracao") Optional<String> duracao) {
    if (Senders.isSelf(sender, target)) {
      this.notifier.sendCannotMuteSelf(sender);
      return CommandResult.invalidUsage();
    }

    var outcome = this.service.mute(target, duracao.orElse(""));
    return switch (outcome) {
      case MuteOutcome.Exempt exempt -> {
        this.notifier.sendExempt(sender, exempt.targetName());
        yield CommandResult.denied();
      }
      case MuteOutcome.InvalidDuration invalid -> {
        this.notifier.sendInvalidDuration(sender, invalid.raw());
        yield CommandResult.invalidUsage();
      }
      case MuteOutcome.Muted muted -> {
        this.notifier.sendMuted(sender, target, muted.mute());
        yield CommandResult.success();
      }
    };
  }
}
