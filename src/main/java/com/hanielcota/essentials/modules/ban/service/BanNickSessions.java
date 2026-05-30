package com.hanielcota.essentials.modules.ban.service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/**
 * Tracks which staff members are currently typing a name into chat for an offline ban. Pure state —
 * the chat listener consumes the session and quit cleanup lives in {@code BanMenuCleanupListener}.
 */
public final class BanNickSessions {

  private final Set<UUID> awaiting = ConcurrentHashMap.newKeySet();

  public void start(@NonNull UUID viewer) {
    this.awaiting.add(viewer);
  }

  public boolean consume(@NonNull UUID viewer) {
    return this.awaiting.remove(viewer);
  }

  public void cancel(@NonNull UUID viewer) {
    this.awaiting.remove(viewer);
  }
}
