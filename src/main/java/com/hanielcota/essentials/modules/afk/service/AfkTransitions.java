package com.hanielcota.essentials.modules.afk.service;

import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

/**
 * Pairs AFK state transitions with the broadcaster. The two operations always travel together —
 * keeping the pairing here means callers never have to remember to fire the broadcast after a
 * successful enter/exit, and the checker/listener/command stay focused on their own concerns.
 */
@RequiredArgsConstructor
public final class AfkTransitions {

  private final AfkService service;
  private final AfkBroadcaster broadcaster;

  /** Returns {@code true} when the player transitioned from non-AFK to AFK. */
  public boolean enter(@NonNull UUID id, @NonNull String name, @Nullable String reason) {
    var transitioned = this.service.enter(id, reason);
    if (!transitioned) {
      return false;
    }

    this.broadcaster.broadcastEnter(name, reason);

    return true;
  }

  /** Returns {@code true} when the player transitioned from AFK to non-AFK. */
  public boolean exit(@NonNull UUID id, @NonNull String name) {
    var transitioned = this.service.exit(id);
    if (!transitioned) {
      return false;
    }

    this.broadcaster.broadcastExit(name);

    return true;
  }
}
