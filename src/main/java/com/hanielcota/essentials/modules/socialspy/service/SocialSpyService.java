package com.hanielcota.essentials.modules.socialspy.service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/** Runtime-only registry of players with social spy active. State is dropped on plugin disable. */
public final class SocialSpyService {

  private final Set<UUID> spies = ConcurrentHashMap.newKeySet();

  public boolean isSpying(@NonNull UUID id) {
    return this.spies.contains(id);
  }

  /** Returns {@code true} when the player was not already spying. */
  public boolean enter(@NonNull UUID id) {
    return this.spies.add(id);
  }

  /** Returns {@code true} when the player was previously spying. */
  public boolean exit(@NonNull UUID id) {
    return this.spies.remove(id);
  }

  /** Snapshot of currently active spy ids. */
  public Set<UUID> spies() {
    return Set.copyOf(this.spies);
  }
}
