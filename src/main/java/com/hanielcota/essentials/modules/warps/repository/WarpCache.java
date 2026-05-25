package com.hanielcota.essentials.modules.warps.repository;

import com.hanielcota.essentials.modules.warps.domain.Warp;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentSkipListMap;
import lombok.NonNull;

/**
 * In-memory index of every warp, populated eagerly at module enable so {@code /warp} and {@code
 * /warps} never hit SQL on the main thread. Keyed case-insensitively to match the SQLite {@code
 * COLLATE NOCASE} on {@code warps.name}.
 */
public final class WarpCache {

  private final ConcurrentSkipListMap<String, Warp> entries =
      new ConcurrentSkipListMap<>(String.CASE_INSENSITIVE_ORDER);

  public void loadAll(@NonNull Collection<Warp> warps) {
    this.entries.clear();
    for (var warp : warps) {
      this.entries.put(warp.name(), warp);
    }
  }

  public Optional<Warp> find(@NonNull String name) {
    return Optional.ofNullable(this.entries.get(name));
  }

  /** Warps sorted by name (case-insensitive), snapshot-safe. */
  public List<Warp> list() {
    return List.copyOf(this.entries.values());
  }

  public void put(@NonNull Warp warp) {
    this.entries.put(warp.name(), warp);
  }

  /** Returns the removed warp (with its canonical-case name) so callers can persist the delete. */
  public Optional<Warp> remove(@NonNull String name) {
    return Optional.ofNullable(this.entries.remove(name));
  }
}
