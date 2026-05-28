package com.hanielcota.essentials.modules.back.service;

import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.HistoryEntry;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/**
 * Stores the history snapshot that {@code /back} read on the main thread so the menu can render it
 * without a second SQL hit on the click thread. Used by the menu via {@link BackEntryProvider},
 * cleared on inventory close / quit.
 */
public final class BackPrefetch {

  private final Map<UUID, List<HistoryEntry>> prefetched = new ConcurrentHashMap<>();

  public void prefetch(@NonNull UUID viewer, @NonNull List<HistoryEntry> entries) {
    this.prefetched.put(viewer, List.copyOf(entries));
  }

  public List<HistoryEntry> consume(@NonNull UUID viewer) {
    return this.prefetched.remove(viewer);
  }

  public void clear(@NonNull UUID viewer) {
    this.prefetched.remove(viewer);
  }
}
