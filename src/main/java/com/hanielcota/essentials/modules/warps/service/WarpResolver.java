package com.hanielcota.essentials.modules.warps.service;

import com.hanielcota.essentials.modules.warps.domain.Warp;
import com.hanielcota.essentials.paper.WorldLookup;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

/**
 * Materializes a {@link Warp} record back into a Bukkit {@link Location}. Extracted from the domain
 * record so the {@code Warp} stays a pure value carrier and the world lookup is testable.
 */
@RequiredArgsConstructor
public final class WarpResolver {

  private final @NonNull WorldLookup worldLookup;

  public Optional<Location> resolve(@NonNull Warp warp) {
    var worldHandle = this.worldLookup.world(warp.world());
    if (worldHandle.isEmpty()) {
      return Optional.empty();
    }

    var location =
        new Location(worldHandle.get(), warp.x(), warp.y(), warp.z(), warp.yaw(), warp.pitch());
    return Optional.of(location);
  }
}
