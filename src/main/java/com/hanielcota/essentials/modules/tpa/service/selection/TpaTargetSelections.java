package com.hanielcota.essentials.modules.tpa.service.selection;

import com.hanielcota.essentials.modules.tpa.domain.TpaTargetSelection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Tracks which player each viewer is acting on in the target-action menu opened by {@code /tpa
 * <nick>} or {@code /tpahere <nick>}. Mirrors {@link TpaFavoriteSelections} but stores an arbitrary
 * target instead of a favorite entry. Cleared on menu close and player quit.
 */
public final class TpaTargetSelections {

  private final ConcurrentHashMap<UUID, TpaTargetSelection> selections = new ConcurrentHashMap<>();

  public void select(@NonNull UUID viewer, @NonNull TpaTargetSelection selection) {
    this.selections.put(viewer, selection);
  }

  public @Nullable TpaTargetSelection of(@NonNull UUID viewer) {
    return this.selections.get(viewer);
  }

  public void clear(@NonNull UUID viewer) {
    this.selections.remove(viewer);
  }
}
