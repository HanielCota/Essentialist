package com.hanielcota.essentials.modules.nick.command;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.nick.config.NickConfig;
import com.hanielcota.essentials.modules.nick.service.NickResetOutcome;
import com.hanielcota.essentials.modules.nick.service.NickSetOutcome;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Owns every user-visible message emitted by {@code /nick}: validation errors, taken-nick errors
 * and the dual sender/target success messaging for both set and reset. Two narrow outcome types
 * keep both switches exhaustive without {@code default} arms.
 */
@RequiredArgsConstructor
public final class NickNotifier {

  private final ConfigHandle<NickConfig> config;
  private final PaperCommandFramework framework;

  public void sendSetOutcome(
      @NonNull CommandActor sender,
      @NonNull Player subject,
      boolean self,
      @NonNull NickSetOutcome outcome) {
    var snap = this.config.value();
    switch (outcome) {
      case NickSetOutcome.InvalidLength ignored -> sender.sendError(snap.formatInvalidLength());
      case NickSetOutcome.InvalidChars ignored -> sender.sendError(snap.invalidChars());
      case NickSetOutcome.Taken ignored -> sender.sendError(snap.nickTaken());
      case NickSetOutcome.Ok ok -> sendSetOk(sender, subject, self, ok.nickname(), ok.realName());
    }
  }

  public void sendResetOutcome(
      @NonNull CommandActor sender,
      @NonNull Player subject,
      boolean self,
      @NonNull NickResetOutcome outcome) {
    var snap = this.config.value();
    switch (outcome) {
      case ALREADY_HAS_NO_NICK -> sender.sendError(snap.alreadyHasNoNick());
      case OK -> sendResetOk(sender, subject, self, snap);
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
