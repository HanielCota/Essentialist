package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.modules.tpa.domain.TpaFavorite;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Tracks which favorite each viewer is currently inspecting in the favorite-action sub-menu.
 *
 * <p>The menu framework has no per-session arbitrary payload, so the click handler stores the
 * selected favorite here before switching to the action menu, and the action menu reads it back on
 * each render. Cleaned up on menu close and player quit.
 */
public final class TpaFavoriteSelections {

  private final ConcurrentHashMap<UUID, TpaFavorite> selections = new ConcurrentHashMap<>();

  public void select(@NonNull UUID viewer, @NonNull TpaFavorite favorite) {
    this.selections.put(viewer, favorite);
  }

  public @Nullable TpaFavorite of(@NonNull UUID viewer) {
    return this.selections.get(viewer);
  }

  public void clear(@NonNull UUID viewer) {
    this.selections.remove(viewer);
  }
}
