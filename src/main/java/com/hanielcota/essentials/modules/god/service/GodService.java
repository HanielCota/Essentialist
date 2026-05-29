package com.hanielcota.essentials.modules.god.service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/**
 * Session state for which players are in god mode. Invulnerability itself is enforced by cancelling
 * damage in the listener, so this service never touches live player state — keeping it pure UUID
 * bookkeeping (and unit-testable without a server).
 */
public final class GodService {

  private final Set<UUID> god = ConcurrentHashMap.newKeySet();

  public boolean isGod(@NonNull UUID id) {
    return this.god.contains(id);
  }

  /** Flips the flag and returns the new state ({@code true} when god is now on). */
  public boolean toggle(@NonNull UUID id) {
    if (this.god.add(id)) {
      return true;
    }

    this.god.remove(id);
    return false;
  }

  /** Turns god on; returns {@code true} when it was not already on. */
  public boolean enable(@NonNull UUID id) {
    return this.god.add(id);
  }

  /** Turns god off; returns {@code true} when it was on. */
  public boolean disable(@NonNull UUID id) {
    return this.god.remove(id);
  }

  /** Drops the session entry for {@code id} (used on quit). */
  public void forget(@NonNull UUID id) {
    this.god.remove(id);
  }
}
