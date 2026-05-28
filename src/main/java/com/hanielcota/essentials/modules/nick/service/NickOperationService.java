package com.hanielcota.essentials.modules.nick.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.nick.config.NickConfig;
import com.hanielcota.essentials.modules.nick.domain.NickResetOutcome;
import com.hanielcota.essentials.modules.nick.domain.NickSetOutcome;
import com.hanielcota.essentials.paper.PlayerProvider;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Orchestrates the {@link NickValidator} → {@link NickService} → {@link NickApplier} chain for set
 * and reset operations. Returns a {@link NickSetOutcome} / {@link NickResetOutcome} the command can
 * route to {@code NickNotifier}. Owns no formatting and no messaging.
 */
@RequiredArgsConstructor
public final class NickOperationService {

  private final ConfigHandle<NickConfig> config;
  private final NickService service;
  private final PlayerProvider players;

  public NickSetOutcome set(@NonNull Player subject, @NonNull String nickname) {
    var snap = this.config.value();
    var validation = NickValidator.check(nickname, snap.minLength(), snap.maxLength());
    if (validation == NickValidator.Result.TOO_SHORT
        || validation == NickValidator.Result.TOO_LONG) {
      return new NickSetOutcome.InvalidLength();
    }
    if (validation == NickValidator.Result.INVALID_CHARS) {
      return new NickSetOutcome.InvalidChars();
    }

    var subjectId = subject.getUniqueId();
    if (this.service.isTakenByOther(nickname, subjectId)) {
      return new NickSetOutcome.Taken();
    }
    if (impersonatesRealPlayer(nickname, subjectId)) {
      return new NickSetOutcome.Taken();
    }

    var realName = subject.getName();
    this.service.set(subjectId, nickname, realName);
    NickApplier.apply(subject, nickname);

    return new NickSetOutcome.Ok(nickname, realName);
  }

  public NickResetOutcome reset(@NonNull Player subject) {
    var subjectId = subject.getUniqueId();
    var removed = this.service.reset(subjectId);
    if (!removed) {
      return NickResetOutcome.ALREADY_HAS_NO_NICK;
    }

    NickApplier.reset(subject);

    return NickResetOutcome.OK;
  }

  // A nickname matching a known player's real name (other than the subject) would let one player
  // impersonate that account, so reject it. Names the server has never resolved can't be checked
  // and are allowed through.
  private boolean impersonatesRealPlayer(@NonNull String nickname, @NonNull UUID subjectId) {
    var known = this.players.offlineByName(nickname);
    if (known.isEmpty()) {
      return false;
    }

    var owner = known.get();
    var ownerId = owner.getUniqueId();

    return !ownerId.equals(subjectId);
  }
}
