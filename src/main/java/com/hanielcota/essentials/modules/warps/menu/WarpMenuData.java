package com.hanielcota.essentials.modules.warps.menu;

import com.hanielcota.essentials.modules.warps.config.WarpsConfig;
import com.hanielcota.essentials.modules.warps.service.WarpFavorites;
import com.hanielcota.essentials.modules.warps.service.WarpLikes;
import com.hanielcota.essentials.modules.warps.service.WarpOccupancy;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Resolves the filter inputs for one viewer, backing {@link WarpFilters} in the live menu. */
@RequiredArgsConstructor
final class WarpMenuData implements WarpFilterData {

  private final UUID viewerId;
  private final WarpsConfig snapshot;
  private final WarpOccupancy occupancy;
  private final WarpLikes likes;
  private final WarpFavorites favorites;

  @Override
  public int players(@NonNull String warpName) {
    return this.occupancy.count(warpName);
  }

  @Override
  public int likes(@NonNull String warpName) {
    return this.likes.count(warpName);
  }

  @Override
  public boolean favorite(@NonNull String warpName) {
    return this.favorites.isFavorite(this.viewerId, warpName);
  }

  @Override
  public boolean pvp(@NonNull String warpName) {
    return this.snapshot.isPvp(warpName);
  }
}
