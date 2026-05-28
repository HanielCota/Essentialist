package com.hanielcota.essentials.modules.warps.menu;

import lombok.NonNull;

/**
 * Per-render data the warps filters need, resolved for the current viewer. Keeps {@link
 * WarpFilters} free of the occupancy/likes/favorites/config wiring (and trivially unit-testable).
 */
public interface WarpFilterData {

  int players(@NonNull String warpName);

  int likes(@NonNull String warpName);

  boolean favorite(@NonNull String warpName);

  boolean pvp(@NonNull String warpName);
}
