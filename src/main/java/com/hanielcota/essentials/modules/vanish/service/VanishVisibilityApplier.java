package com.hanielcota.essentials.modules.vanish.service;

import com.hanielcota.essentials.EssentialsPlugin;
import com.hanielcota.essentials.paper.PlayerProvider;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Applies and rolls back the visual + protection state of a vanished player.
 *
 * <p>Visibility uses Paper's {@code Player#hidePlayer(Plugin, Player)} which also hides the tab
 * entry, so no packet plumbing is needed. Viewers with {@code essentials.vanish.see} keep seeing
 * vanished players (staff observation). Invulnerability and pickup suppression are flag-toggles on
 * the player itself; mob targeting is filtered by {@link
 * com.hanielcota.essentials.modules.vanish.listener.VanishProtectionListener} since hidePlayer does
 * not affect mob AI.
 */
@RequiredArgsConstructor
public final class VanishVisibilityApplier {

  public static final String SEE_PERMISSION = "essentials.vanish.see";

  private final EssentialsPlugin plugin;
  private final PlayerProvider players;
  private final Map<UUID, Boolean> previousInvulnerable = new ConcurrentHashMap<>();
  private final Map<UUID, Boolean> previousPickup = new ConcurrentHashMap<>();

  /** Hides {@code target} from every viewer without {@link #SEE_PERMISSION} and enables guards. */
  public void apply(@NonNull Player target) {
    var viewers = this.players.all();

    for (var viewer : viewers) {
      if (viewer.equals(target)) {
        continue;
      }
      if (viewer.hasPermission(SEE_PERMISSION)) {
        continue;
      }
      viewer.hidePlayer(this.plugin, target);
    }

    var id = target.getUniqueId();
    this.previousInvulnerable.put(id, target.isInvulnerable());
    this.previousPickup.put(id, target.getCanPickupItems());

    target.setInvulnerable(true);
    target.setCanPickupItems(false);
  }

  /**
   * Reveals {@code target} to every viewer and restores the protection flags to pre-vanish state.
   */
  public void unapply(@NonNull Player target) {
    var viewers = this.players.all();

    for (var viewer : viewers) {
      if (viewer.equals(target)) {
        continue;
      }
      viewer.showPlayer(this.plugin, target);
    }

    var id = target.getUniqueId();
    var wasInvulnerable = this.previousInvulnerable.remove(id);
    var couldPickup = this.previousPickup.remove(id);

    target.setInvulnerable(wasInvulnerable != null && wasInvulnerable);
    target.setCanPickupItems(couldPickup == null || couldPickup);
  }

  /**
   * Hides every currently-vanished player from {@code viewer}. Called when a new player joins so
   * they see the world consistent with existing vanish state.
   */
  public void hideExistingFor(@NonNull Player viewer, @NonNull Iterable<UUID> vanishedIds) {
    if (viewer.hasPermission(SEE_PERMISSION)) {
      return;
    }

    for (var id : vanishedIds) {
      var target = this.players.online(id).orElse(null);
      if (target == null) {
        continue;
      }
      if (target.equals(viewer)) {
        continue;
      }
      viewer.hidePlayer(this.plugin, target);
    }
  }
}
