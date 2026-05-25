package com.hanielcota.essentials.core.api;

import com.hanielcota.essentials.api.WarpsApi;
import com.hanielcota.essentials.modules.warps.domain.Warp;
import com.hanielcota.essentials.modules.warps.service.WarpService;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.permissions.Permissible;

@RequiredArgsConstructor
public final class WarpsApiAdapter implements WarpsApi {

  private final WarpService service;

  @Override
  public List<Warp> warps() {
    return this.service.list();
  }

  @Override
  public Optional<Warp> findWarp(@NonNull String name) {
    return this.service.find(name);
  }

  @Override
  public List<Warp> visibleTo(@NonNull Permissible viewer) {
    return this.service.listVisibleTo(viewer);
  }

  @Override
  public boolean canUse(@NonNull Permissible viewer, @NonNull String name) {
    return this.service.canUse(viewer, name);
  }
}
