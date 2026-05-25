package com.hanielcota.essentials.modules.vanish.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Pairs vanish state mutations with the visibility applier. The state add/remove and the visual
 * apply/unapply always travel together — keeping the pairing here means commands and listeners
 * never have to remember to keep them in sync.
 */
@RequiredArgsConstructor
public final class VanishTransitions {

  private final VanishService service;
  private final VanishVisibilityApplier applier;

  /**
   * Toggles vanish for the given player. Returns {@code true} when the player is now vanished,
   * {@code false} when they were just revealed.
   */
  public boolean toggle(@NonNull Player target) {
    var id = target.getUniqueId();
    var entering = this.service.enter(id);
    if (entering) {
      this.applier.apply(target);

      return true;
    }

    this.service.exit(id);
    this.applier.unapply(target);

    return false;
  }

  /**
   * Clears vanish state for a player that is leaving the server. No-op when the player is not
   * vanished. Used by the quit listener so {@code setInvulnerable}/pickup flags don't persist into
   * NBT for the next session.
   */
  public void leave(@NonNull Player target) {
    var id = target.getUniqueId();
    if (!this.service.isVanished(id)) {
      return;
    }

    this.applier.unapply(target);
    this.service.exit(id);
  }
}
