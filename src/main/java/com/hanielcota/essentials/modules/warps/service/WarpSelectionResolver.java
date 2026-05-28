package com.hanielcota.essentials.modules.warps.service;

import com.hanielcota.essentials.modules.warps.domain.Warp;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Resolves the warp a player is currently acting on in the action/occupants submenus: reads the
 * name from {@link WarpSelection} and looks the warp up. Shared by the action renderer and click
 * handler so neither re-implements the lookup.
 */
@RequiredArgsConstructor
public final class WarpSelectionResolver {

  private final WarpSelection selection;
  private final WarpService service;

  public @NonNull Optional<Warp> resolve(@NonNull UUID playerId) {
    var name = this.selection.of(playerId);
    if (name == null) {
      return Optional.empty();
    }

    return this.service.findWarp(name);
  }
}
