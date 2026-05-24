package com.hanielcota.essentials.modules.tpa.menu;

import com.hanielcota.essentials.modules.tpa.history.TpaHistoryEntry;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

public final class TpaHistoryMenuState {

  private final Map<UUID, List<TpaHistoryEntry>> prefetched = new ConcurrentHashMap<>();

  public void prefetch(@NonNull UUID viewer, @NonNull List<TpaHistoryEntry> entries) {
    this.prefetched.put(viewer, List.copyOf(entries));
  }

  public List<TpaHistoryEntry> consumePrefetch(@NonNull UUID viewer) {
    return this.prefetched.remove(viewer);
  }

  public void clear(@NonNull UUID viewer) {
    this.prefetched.remove(viewer);
  }
}
