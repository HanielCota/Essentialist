package com.hanielcota.essentials.modules.chat.service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/**
 * Tracks which staff members have persistent staff-chat toggled on. Backed by a {@link
 * ConcurrentHashMap#newKeySet() concurrent set keyset} because both the async chat thread and the
 * main thread (via {@code /staffchat toggle}) read and write it.
 *
 * <p>State is in-memory and session-scoped — the spec says "persistente por sessão". On quit the
 * quit listener clears the entry so a returning player starts in normal chat unless they re-toggle.
 */
public final class StaffChatToggleService {

  private final Set<UUID> active = ConcurrentHashMap.newKeySet();

  public boolean isActive(@NonNull UUID id) {
    return this.active.contains(id);
  }

  /**
   * Flips the toggle. Returns {@code true} if staff chat is now ACTIVE for {@code id}, {@code
   * false} if it was just turned off.
   */
  public boolean toggle(@NonNull UUID id) {
    if (this.active.remove(id)) {
      return false;
    }

    this.active.add(id);

    return true;
  }

  public void clear(@NonNull UUID id) {
    this.active.remove(id);
  }
}
