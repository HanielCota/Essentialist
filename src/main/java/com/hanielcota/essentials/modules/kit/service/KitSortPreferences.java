package com.hanielcota.essentials.modules.kit.service;

import com.hanielcota.essentials.modules.kit.domain.KitSort;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/** Per-viewer kit list sort order, defaulting to {@link KitSort#NAME}. */
public final class KitSortPreferences {

  private final Map<UUID, KitSort> byPlayer = new ConcurrentHashMap<>();

  public KitSort of(@NonNull UUID viewer) {
    return this.byPlayer.getOrDefault(viewer, KitSort.NAME);
  }

  public void cycle(@NonNull UUID viewer) {
    var next = of(viewer).next();

    this.byPlayer.put(viewer, next);
  }

  public void clear(@NonNull UUID viewer) {
    this.byPlayer.remove(viewer);
  }
}
