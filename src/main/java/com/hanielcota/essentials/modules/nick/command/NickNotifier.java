package com.hanielcota.essentials.modules.nick.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.nick.config.NickConfig;
import com.hanielcota.essentials.modules.nick.service.NickOutcome;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Owns every user-visible message emitted by {@code /nick}: validation errors, taken-nick errors
 * and the dual sender/target success messaging for both set and reset. Keeping these here lets the
 * command stay a thin dispatcher and the operation service stay a pure orchestrator.
 */
@RequiredArgsConstructor
public final class NickNotifier {

  private final ConfigHandle<NickConfig> config;
  private final PaperCommandFramework framework;

  public void sendSetOutcome(
      @NonNull CommandActor sender,
      @NonNull Player subject,
      boolean self,
      @NonNull NickOutcome outcome) {
    var snap = this.config.value();
    switch (outcome) {
      case NickOutcome.InvalidLength ignored -> {
        var msg = snap.formatInvalidLength();
        sender.sendError(msg);
      }
      case NickOutcome.InvalidChars ignored -> sender.sendError(snap.invalidChars());
      case NickOutcome.Taken ignored -> sender.sendError(snap.nickTaken());
      case NickOutcome.SetOk ok -> sendSetOk(sender, subject, self, ok.nickname(), ok.realName());
      default -> throw new IllegalStateException("unexpected outcome for set: " + outcome);
    }
  }

  public void sendResetOutcome(
      @NonNull CommandActor sender,
      @NonNull Player subject,
      boolean self,
      @NonNull NickOutcome outcome) {
    var snap = this.config.value();
    switch (outcome) {
      case NickOutcome.AlreadyHasNoNick ignored -> sender.sendError(snap.alreadyHasNoNick());
      case NickOutcome.ResetOk ignored -> sendResetOk(sender, subject, self, snap);
      default -> throw new IllegalStateException("unexpected outcome for reset: " + outcome);
    }
  }

  private void sendSetOk(
      @NonNull CommandActor sender,
      @NonNull Player subject,
      boolean self,
      @NonNull String nickname,
      @NonNull String realName) {
    var snap = this.config.value();

    if (self) {
      var selfMsg = snap.formatNickSetSelf(nickname);
      sender.sendMessage(selfMsg);
      return;
    }

    var senderMsg = snap.formatNickSetOther(realName, nickname);
    var targetMsg = snap.formatNickSetByOther(nickname);
    var subjectActor = this.framework.actorOf(subject);

    sender.sendMessage(senderMsg);
    subjectActor.sendMessage(targetMsg);
  }

  private void sendResetOk(
      @NonNull CommandActor sender,
      @NonNull Player subject,
      boolean self,
      @NonNull NickConfig snap) {
    if (self) {
      var selfMsg = snap.nickResetSelf();
      sender.sendMessage(selfMsg);
      return;
    }

    var subjectName = subject.getName();
    var senderMsg = snap.formatNickResetOther(subjectName);
    var targetMsg = snap.nickResetByOther();
    var subjectActor = this.framework.actorOf(subject);

    sender.sendMessage(senderMsg);
    subjectActor.sendMessage(targetMsg);
  }
}
