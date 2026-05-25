package com.hanielcota.essentials.modules.mute.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.mute.config.MuteConfig;
import com.hanielcota.essentials.modules.mute.model.Mute;
import com.hanielcota.essentials.modules.mute.service.MuteService;
import com.hanielcota.essentials.util.DurationFormatter;
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
import io.github.hanielcota.commandframework.core.util.TimeParser;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.time.Duration;
import java.time.Instant;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.jspecify.annotations.Nullable;

@Command("mute")
@Permission("essentials.mute")
@Description("Silencia o chat de um jogador.")
@Syntax("/mute <jogador> [duração]")
public record MuteCommand(
    ConfigHandle<MuteConfig> config, MuteService service, PaperCommandFramework framework) {

  private static final String EXEMPT_PERMISSION = "essentials.mute.exempt";

  @DefaultSubcommand
  public void execute(
      @NonNull CommandActor sender,
      @OnlinePlayer @NonNull Player target,
      @DefaultValue("") @GreedyString @Arg("duracao") String duracao) {
    var snap = this.config.value();
    var name = target.getName();
    var trimmed = duracao.strip();

    if (Senders.isSelf(sender, target)) {
      var selfMsg = snap.cannotMuteSelf();
      sender.sendError(selfMsg);
      return;
    }

    if (target.hasPermission(EXEMPT_PERMISSION)) {
      var exemptMsg = snap.formatExempt(name);
      sender.sendError(exemptMsg);
      return;
    }

    Duration duration = null;
    if (!trimmed.isEmpty()) {
      duration = tryParseDuration(trimmed);
      if (duration == null) {
        var invalidMsg = snap.formatInvalidDuration(trimmed);
        sender.sendError(invalidMsg);
        return;
      }
    }

    var targetId = target.getUniqueId();
    var mute = buildMute(duration);
    this.service.mute(targetId, mute);

    var targetActor = this.framework.actorOf(target);

    if (mute.isPermanent()) {
      var senderMsg = snap.formatMutedSender(name);
      var targetMsg = snap.mutedTarget();

      sender.sendMessage(senderMsg);
      targetActor.sendMessage(targetMsg);
      return;
    }

    var timeStr = DurationFormatter.format(duration);
    var senderMsg = snap.formatMutedSenderTimed(name, timeStr);
    var targetMsg = snap.formatMutedTargetTimed(timeStr);

    sender.sendMessage(senderMsg);
    targetActor.sendMessage(targetMsg);
  }

  private static @Nullable Duration tryParseDuration(@NonNull String input) {
    try {
      return TimeParser.parse(input);
    } catch (RuntimeException ignored) {
      return null;
    }
  }

  private static Mute buildMute(@Nullable Duration duration) {
    if (duration == null) {
      return Mute.permanent();
    }

    var now = Instant.now();
    var expiry = now.plus(duration);

    return Mute.until(expiry);
  }
}
