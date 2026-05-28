package com.hanielcota.essentials.modules.vanish.service;

import com.hanielcota.essentials.paper.PlayerProvider;
import lombok.RequiredArgsConstructor;

/**
 * Reverts all active vanish states on plugin disable. The NBT-persisted attributes ({@code
 * setInvulnerable}, {@code setCanPickupItems}) must be restored or players rejoin permanently
 * invulnerable. Extracted from {@code VanishModule.onDisable()} so the module stays thin and the
 * cleanup is testable in isolation.
 */
@RequiredArgsConstructor
public final class VanishCleanupService {

  private final VanishService service;
  private final VanishVisibilityApplier applier;
  private final PlayerProvider players;

  public void revertAll() {
    var vanishedIds = this.service.vanished();
    for (var id : vanishedIds) {
      var player = this.players.online(id).orElse(null);
      if (player == null) {
        continue;
      }
      this.applier.unapply(player);
    }
  }
}
