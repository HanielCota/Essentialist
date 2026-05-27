package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.modules.tpa.domain.TpaPickPlayerFilter;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/**
 * In-memory store of the current {@link TpaPickPlayerFilter} selected by each viewer in the
 * pick-player menu. Cleared on inventory close so the menu opens with {@link
 * TpaPickPlayerFilter#ALL} every time.
 */
public final class TpaPickPlayerFilters {

  private final ConcurrentHashMap<UUID, TpaPickPlayerFilter> filters = new ConcurrentHashMap<>();

  public TpaPickPlayerFilter of(@NonNull UUID viewer) {
    return this.filters.getOrDefault(viewer, TpaPickPlayerFilter.ALL);
  }

  public TpaPickPlayerFilter cycle(@NonNull UUID viewer) {
    return this.filters.compute(
        viewer, (id, current) -> current == null ? TpaPickPlayerFilter.ALL.next() : current.next());
  }

  public void clear(@NonNull UUID viewer) {
    this.filters.remove(viewer);
  }
}
