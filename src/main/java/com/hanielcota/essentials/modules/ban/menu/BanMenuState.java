package com.hanielcota.essentials.modules.ban.menu;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

/** Per-viewer state for the ban-options menu: the ban being composed. */
public final class BanMenuState {

  private final Map<UUID, BanSelection> selections = new ConcurrentHashMap<>();

  public void begin(
      @NonNull UUID viewer,
      @NonNull UUID targetId,
      @NonNull String targetName,
      @NonNull String permanentLabel) {
    var selection = new BanSelection(targetId, targetName, "", permanentLabel, null);

    this.selections.put(viewer, selection);
  }

  public @Nullable BanSelection get(@NonNull UUID viewer) {
    return this.selections.get(viewer);
  }

  public void setDuration(@NonNull UUID viewer, @NonNull String raw, @NonNull String label) {
    this.selections.computeIfPresent(viewer, (id, current) -> current.withDuration(raw, label));
  }

  public void setReason(@NonNull UUID viewer, @NonNull String reason) {
    this.selections.computeIfPresent(viewer, (id, current) -> current.withReason(reason));
  }

  public void clear(@NonNull UUID viewer) {
    this.selections.remove(viewer);
  }
}
