package com.hanielcota.essentials.modules.back.service;

import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.HistoryEntry;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Single source of truth the {@link com.hanielcota.essentials.modules.back.menu.BackMenu} consults
 * to render an entry list: reuses the prefetch left by {@code /back}, falling back to a direct
 * {@link TeleportHistory#list(UUID)} only when no snapshot exists. Reusing (rather than draining)
 * the snapshot keeps filter re-renders off the SQL thread. Centralizing the lookup hides the cache
 * vs SQL choice from the menu.
 */
@RequiredArgsConstructor
public final class BackEntryProvider {

  private final @NonNull BackPrefetch prefetch;
  private final @NonNull TeleportHistory history;

  public List<HistoryEntry> entriesFor(@NonNull UUID viewer) {
    var cached = this.prefetch.peek(viewer);
    if (cached != null) {
      return cached;
    }
    return this.history.list(viewer);
  }
}
