package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Tracks which pending request each viewer is currently acting on in the action sub-menu.
 *
 * <p>The menu framework has no per-session arbitrary payload, so the click handler stores the
 * selected request here before switching to {@code TpaPendingActionMenu}, and the action menu reads
 * it back on each render. Cleaned up on back-click, action, and viewer quit.
 */
public final class TpaPendingSelections {

  private final ConcurrentHashMap<UUID, TeleportRequest> selections = new ConcurrentHashMap<>();

  public void select(@NonNull UUID viewer, @NonNull TeleportRequest request) {
    this.selections.put(viewer, request);
  }

  public @Nullable TeleportRequest of(@NonNull UUID viewer) {
    return this.selections.get(viewer);
  }

  public void clear(@NonNull UUID viewer) {
    this.selections.remove(viewer);
  }
}
