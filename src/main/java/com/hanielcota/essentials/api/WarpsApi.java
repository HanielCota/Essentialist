package com.hanielcota.essentials.api;

import com.hanielcota.essentials.modules.warps.domain.Warp;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import org.bukkit.permissions.Permissible;

/** Read-only access to server warps. Available when the {@code warps} module is enabled. */
public interface WarpsApi {

  /** All registered warps, sorted by name (case-insensitive). */
  List<Warp> warps();

  /** Specific warp by name (case-insensitive), or empty if not found. */
  Optional<Warp> findWarp(@NonNull String name);

  /**
   * Warps {@code viewer} has permission to use — i.e. holds {@code essentials.warp.use.<name>} or
   * the wildcard {@code essentials.warp.use.*}.
   */
  List<Warp> visibleTo(@NonNull Permissible viewer);

  /** Whether {@code viewer} may use the warp named {@code name}. */
  boolean canUse(@NonNull Permissible viewer, @NonNull String name);
}
