package com.hanielcota.essentials.modules.back.service;

import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.Cause;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

public final class BackFilterState {

  private final Map<UUID, Cause> filters = new ConcurrentHashMap<>();

  private static @Nullable Cause nextFilter(@Nullable Cause current) {
    if (current == null) {
      return Cause.DEATH;
    }
    return switch (current) {
      case DEATH -> Cause.TELEPORT;
      case TELEPORT -> null;
    };
  }

  public @Nullable Cause filterOf(@NonNull UUID viewer) {
    return this.filters.get(viewer);
  }

  /** Advances the viewer's filter through {@code all -> DEATH -> TELEPORT -> all}. */
  public @Nullable Cause cycleFilter(@NonNull UUID viewer) {
    var current = this.filters.get(viewer);
    var next = nextFilter(current);

    if (next == null) {
      this.filters.remove(viewer);
      return null;
    }

    this.filters.put(viewer, next);
    return next;
  }

  public void clear(@NonNull UUID viewer) {
    this.filters.remove(viewer);
  }
}
