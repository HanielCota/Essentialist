package com.hanielcota.essentials.modules.vanish.service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/** Runtime-only registry of vanished players. State is dropped on plugin disable. */
public final class VanishService {

  private final Set<UUID> vanished = ConcurrentHashMap.newKeySet();

  public boolean isVanished(@NonNull UUID id) {
    return this.vanished.contains(id);
  }

  /** Returns {@code true} when the player was not already vanished. */
  public boolean enter(@NonNull UUID id) {
    return this.vanished.add(id);
  }

  /** Returns {@code true} when the player was vanished. */
  public boolean exit(@NonNull UUID id) {
    return this.vanished.remove(id);
  }

  /** Snapshot of currently vanished player ids. */
  public Set<UUID> vanished() {
    return Set.copyOf(this.vanished);
  }

  public int size() {
    return this.vanished.size();
  }
}
