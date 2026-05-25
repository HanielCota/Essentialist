package com.hanielcota.essentials.modules.nick.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.nick.config.NickConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Orchestrates the {@link NickValidator} → {@link NickService} → {@link NickApplier} chain for set
 * and reset operations. Returns a {@link NickOutcome} the command can route to {@code
 * NickNotifier}. Owns no formatting and no messaging.
 */
@RequiredArgsConstructor
public final class NickOperationService {

  private final ConfigHandle<NickConfig> config;
  private final NickService service;

  public NickOutcome set(@NonNull Player subject, @NonNull String nickname) {
    var snap = this.config.value();
    var validation = NickValidator.check(nickname, snap.minLength(), snap.maxLength());
    if (validation == NickValidator.Result.TOO_SHORT
        || validation == NickValidator.Result.TOO_LONG) {
      return new NickOutcome.InvalidLength();
    }
    if (validation == NickValidator.Result.INVALID_CHARS) {
      return new NickOutcome.InvalidChars();
    }

    var subjectId = subject.getUniqueId();
    if (this.service.isTakenByOther(nickname, subjectId)) {
      return new NickOutcome.Taken();
    }

    var realName = subject.getName();
    this.service.set(subjectId, nickname, realName);
    NickApplier.apply(subject, nickname);

    return new NickOutcome.SetOk(nickname, realName);
  }

  public NickOutcome reset(@NonNull Player subject) {
    var subjectId = subject.getUniqueId();
    var removed = this.service.reset(subjectId);
    if (!removed) {
      return new NickOutcome.AlreadyHasNoNick();
    }

    NickApplier.reset(subject);

    return new NickOutcome.ResetOk();
  }
}
