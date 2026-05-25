package com.hanielcota.essentials.modules.vanish.service;

import com.hanielcota.essentials.EssentialsPlugin;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
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

  /** Hides {@code target} from every viewer without {@link #SEE_PERMISSION} and enables guards. */
  public void apply(@NonNull Player target) {
    var viewers = Bukkit.getOnlinePlayers();

    for (var viewer : viewers) {
      if (viewer.equals(target)) {
        continue;
      }
      if (viewer.hasPermission(SEE_PERMISSION)) {
        continue;
      }
      viewer.hidePlayer(this.plugin, target);
    }

    target.setInvulnerable(true);
    target.setCanPickupItems(false);
  }

  /** Reveals {@code target} to every viewer and clears the protection flags. */
  public void unapply(@NonNull Player target) {
    var viewers = Bukkit.getOnlinePlayers();

    for (var viewer : viewers) {
      if (viewer.equals(target)) {
        continue;
      }
      viewer.showPlayer(this.plugin, target);
    }

    target.setInvulnerable(false);
    target.setCanPickupItems(true);
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
      var target = Bukkit.getPlayer(id);
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
