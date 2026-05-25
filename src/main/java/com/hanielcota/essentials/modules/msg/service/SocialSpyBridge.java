package com.hanielcota.essentials.modules.msg.service;

import com.hanielcota.essentials.modules.socialspy.service.SocialSpyBroadcaster;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Forwards delivered private messages to active social spies. The broadcaster is supplied lazily so
 * module load order between {@code msg} and {@code socialspy} does not matter — when socialspy is
 * disabled the forward is a no-op.
 */
@RequiredArgsConstructor
public final class SocialSpyBridge {

  private final Supplier<Optional<SocialSpyBroadcaster>> broadcaster;

  public void notifySpies(
      @NonNull UUID senderId,
      @NonNull String senderName,
      @NonNull UUID targetId,
      @NonNull String targetName,
      @NonNull String body) {
    var spy = this.broadcaster.get().orElse(null);
    if (spy == null) {
      return;
    }

    spy.broadcast(senderId, senderName, targetId, targetName, body);
  }
}
