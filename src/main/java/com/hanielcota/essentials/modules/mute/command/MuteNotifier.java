package com.hanielcota.essentials.modules.mute.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.mute.config.MuteConfig;
import com.hanielcota.essentials.modules.mute.model.Mute;
import com.hanielcota.essentials.util.DurationFormatter;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import java.time.Duration;
import java.time.Instant;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Owns every user-visible message emitted by {@code /mute} and {@code /unmute}: dual sender/target
 * delivery, permanent vs timed formatting and the error/exempt/self lines. Keeping these here lets
 * the commands and the service stay focused on routing and domain state.
 */
@RequiredArgsConstructor
public final class MuteNotifier {

  private final ConfigHandle<MuteConfig> config;
  private final PaperCommandFramework framework;

  public void sendCannotMuteSelf(@NonNull CommandActor sender) {
    var snap = this.config.value();
    var selfMsg = snap.cannotMuteSelf();

    sender.sendError(selfMsg);
  }

  public void sendExempt(@NonNull CommandActor sender, @NonNull String targetName) {
    var snap = this.config.value();
    var exemptMsg = snap.formatExempt(targetName);

    sender.sendError(exemptMsg);
  }

  public void sendInvalidDuration(@NonNull CommandActor sender, @NonNull String raw) {
    var snap = this.config.value();
    var invalidMsg = snap.formatInvalidDuration(raw);

    sender.sendError(invalidMsg);
  }

  public void sendNotMuted(@NonNull CommandActor sender, @NonNull String targetName) {
    var snap = this.config.value();
    var notMutedMsg = snap.formatNotMuted(targetName);

    sender.sendError(notMutedMsg);
  }

  public void sendMuted(@NonNull CommandActor sender, @NonNull Player target, @NonNull Mute mute) {
    var snap = this.config.value();
    var name = target.getName();
    var targetActor = this.framework.actorOf(target);

    if (mute.isPermanent()) {
      var senderMsg = snap.formatMutedSender(name);
      var targetMsg = snap.mutedTarget();

      sender.sendMessage(senderMsg);
      targetActor.sendMessage(targetMsg);
      return;
    }

    var remaining = remainingFrom(mute);
    var timeStr = DurationFormatter.format(remaining);
    var senderMsg = snap.formatMutedSenderTimed(name, timeStr);
    var targetMsg = snap.formatMutedTargetTimed(timeStr);

    sender.sendMessage(senderMsg);
    targetActor.sendMessage(targetMsg);
  }

  public void sendUnmuted(@NonNull CommandActor sender, @NonNull Player target) {
    var snap = this.config.value();
    var name = target.getName();
    var senderMsg = snap.formatUnmutedSender(name);
    var targetMsg = snap.unmutedTarget();
    var targetActor = this.framework.actorOf(target);

    sender.sendMessage(senderMsg);
    targetActor.sendMessage(targetMsg);
  }

  private static Duration remainingFrom(@NonNull Mute mute) {
    var expiresAt = mute.expiresAt();
    if (expiresAt == null) {
      return Duration.ZERO;
    }

    var now = Instant.now();

    return Duration.between(now, expiresAt);
  }
}
