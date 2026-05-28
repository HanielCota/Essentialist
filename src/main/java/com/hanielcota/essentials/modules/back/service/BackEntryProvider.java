package com.hanielcota.essentials.modules.back.service;

import com.hanielcota.essentials.modules.teleport.history.TeleportHistory;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.HistoryEntry;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Single source of truth the {@link com.hanielcota.essentials.modules.back.menu.BackMenu} consults
 * to render an entry list: consumes the prefetch left by {@code /back}, falling back to a direct
 * {@link TeleportHistory#list(UUID)} when the prefetch has already been drained (e.g. page-flip
 * re-renders after the initial open). Centralizing the lookup hides the cache vs SQL choice from
 * the menu.
 */
@RequiredArgsConstructor
public final class BackEntryProvider {

  private final @NonNull BackPrefetch prefetch;
  private final @NonNull TeleportHistory history;

  public List<HistoryEntry> entriesFor(@NonNull UUID viewer) {
    var cached = this.prefetch.consume(viewer);
    if (cached != null) {
      return cached;
    }
    return this.history.list(viewer);
  }
}
