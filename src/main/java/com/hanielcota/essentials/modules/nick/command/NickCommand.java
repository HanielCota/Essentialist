package com.hanielcota.essentials.modules.nick.command;

import com.hanielcota.essentials.command.Senders;
import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.nick.config.NickConfig;
import com.hanielcota.essentials.modules.nick.service.NickApplier;
import com.hanielcota.essentials.modules.nick.service.NickService;
import com.hanielcota.essentials.modules.nick.service.NickValidator;
import io.github.hanielcota.commandframework.annotation.Arg;
import io.github.hanielcota.commandframework.annotation.Command;
import io.github.hanielcota.commandframework.annotation.Cooldown;
import io.github.hanielcota.commandframework.annotation.DefaultSubcommand;
import io.github.hanielcota.commandframework.annotation.Description;
import io.github.hanielcota.commandframework.annotation.Permission;
import io.github.hanielcota.commandframework.annotation.PermissionForOther;
import io.github.hanielcota.commandframework.annotation.Syntax;
import io.github.hanielcota.commandframework.annotation.TargetOrSelf;
import io.github.hanielcota.commandframework.core.CommandActor;
import io.github.hanielcota.commandframework.paper.PaperCommandFramework;
import lombok.NonNull;
import org.bukkit.entity.Player;

@Command("nick")
@Permission("essentials.nick")
@PermissionForOther(".others")
@Cooldown(duration = "3s")
@Description("Define ou remove o apelido de um jogador.")
@Syntax("/nick <nome|off> [jogador]")
public record NickCommand(
    ConfigHandle<NickConfig> config, NickService service, PaperCommandFramework framework) {

  private static final String OFF_KEYWORD = "off";

  @DefaultSubcommand
  public void execute(
      @NonNull CommandActor sender,
      @Arg("nick") String nick,
      @TargetOrSelf @NonNull Player subject) {
    var snap = this.config.value();
    var trimmed = nick.strip();
    var self = Senders.isSelf(sender, subject);

    if (trimmed.equalsIgnoreCase(OFF_KEYWORD)) {
      handleReset(sender, subject, snap, self);
      return;
    }

    handleSet(sender, subject, snap, self, trimmed);
  }

  private void handleReset(
      @NonNull CommandActor sender,
      @NonNull Player subject,
      @NonNull NickConfig snap,
      boolean self) {
    var subjectId = subject.getUniqueId();
    var removed = this.service.reset(subjectId);

    if (!removed) {
      var noNickMsg = snap.alreadyHasNoNick();
      sender.sendError(noNickMsg);
      return;
    }

    NickApplier.reset(subject);

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

  private void handleSet(
      @NonNull CommandActor sender,
      @NonNull Player subject,
      @NonNull NickConfig snap,
      boolean self,
      @NonNull String nickname) {
    var validation = NickValidator.check(nickname, snap.minLength(), snap.maxLength());
    if (validation != NickValidator.Result.OK) {
      var errorMsg = renderValidation(snap, validation);
      sender.sendError(errorMsg);
      return;
    }

    var subjectId = subject.getUniqueId();
    if (this.service.isTakenByOther(nickname, subjectId)) {
      var takenMsg = snap.nickTaken();
      sender.sendError(takenMsg);
      return;
    }

    var realName = subject.getName();
    this.service.set(subjectId, nickname, realName);
    NickApplier.apply(subject, nickname);

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

  private static String renderValidation(
      @NonNull NickConfig snap, @NonNull NickValidator.Result result) {
    return switch (result) {
      case TOO_SHORT, TOO_LONG -> snap.formatInvalidLength();
      case INVALID_CHARS -> snap.invalidChars();
      case OK -> throw new IllegalStateException("OK is not an error");
    };
  }
}
