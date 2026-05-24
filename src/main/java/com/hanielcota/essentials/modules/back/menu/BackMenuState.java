package com.hanielcota.essentials.modules.back.menu;

import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.HistoryEntry;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

public final class BackMenuState {

  private final Map<UUID, List<HistoryEntry>> prefetched = new ConcurrentHashMap<>();

  public void prefetch(@NonNull UUID viewer, @NonNull List<HistoryEntry> entries) {
    this.prefetched.put(viewer, List.copyOf(entries));
  }

  public List<HistoryEntry> consumePrefetch(@NonNull UUID viewer) {
    return this.prefetched.remove(viewer);
  }

  public void clear(@NonNull UUID viewer) {
    this.prefetched.remove(viewer);
  }
}
