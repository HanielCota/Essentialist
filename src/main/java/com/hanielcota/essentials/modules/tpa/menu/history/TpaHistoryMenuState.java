package com.hanielcota.essentials.modules.tpa.menu.history;

import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestStatus;
import com.hanielcota.essentials.modules.tpa.history.TpaHistoryEntry;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

public final class TpaHistoryMenuState {

  private final Map<UUID, List<TpaHistoryEntry>> prefetched = new ConcurrentHashMap<>();
  private final Map<UUID, TeleportRequestStatus> filters = new ConcurrentHashMap<>();

  private static @Nullable TeleportRequestStatus nextFilter(
      @Nullable TeleportRequestStatus current) {
    if (current == null) {
      return TeleportRequestStatus.ACCEPTED;
    }
    return switch (current) {
      case ACCEPTED -> TeleportRequestStatus.DENIED;
      case DENIED -> TeleportRequestStatus.EXPIRED;
      case EXPIRED -> TeleportRequestStatus.CANCELLED;
      case CANCELLED -> null;
    };
  }

  public void prefetch(@NonNull UUID viewer, @NonNull List<TpaHistoryEntry> entries) {
    this.prefetched.put(viewer, List.copyOf(entries));
  }

  public List<TpaHistoryEntry> consumePrefetch(@NonNull UUID viewer) {
    return this.prefetched.remove(viewer);
  }

  public @Nullable TeleportRequestStatus filterOf(@NonNull UUID viewer) {
    return this.filters.get(viewer);
  }

  public void setFilter(@NonNull UUID viewer, @Nullable TeleportRequestStatus filter) {
    if (filter == null) {
      this.filters.remove(viewer);
      return;
    }
    this.filters.put(viewer, filter);
  }

  /**
   * Advances the viewer's filter through {@code null → ACCEPTED → DENIED → EXPIRED → CANCELLED →
   * null}.
   */
  public @Nullable TeleportRequestStatus cycleFilter(@NonNull UUID viewer) {
    var current = this.filters.get(viewer);
    var next = nextFilter(current);
    setFilter(viewer, next);
    return next;
  }

  public void clear(@NonNull UUID viewer) {
    this.prefetched.remove(viewer);
    this.filters.remove(viewer);
  }
}
