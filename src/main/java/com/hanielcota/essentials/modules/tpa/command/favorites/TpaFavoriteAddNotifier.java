package com.hanielcota.essentials.modules.tpa.command.favorites;

import com.hanielcota.essentials.modules.tpa.service.TpaProfileService;
import com.hanielcota.essentials.paper.PlayerProvider;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Tells a player that someone has just added them to their favorites — gated by the target's {@code
 * notifyWhenFavorited} preference and skipped silently if the target is offline.
 *
 * <p>Centralises the opt-in check so every entry point that calls {@link
 * com.hanielcota.essentials.modules.tpa.service.favorites.TpaFavoriteService#add} can plug into the
 * same behaviour: chat prompt, contact-suggestion click and pick-player action menu.
 */
@RequiredArgsConstructor
public final class TpaFavoriteAddNotifier {

  private final TpaFavoriteNotifier notifier;
  private final TpaProfileService profiles;
  private final PlayerProvider players;

  public void notify(@NonNull String ownerName, @NonNull UUID targetId) {
    var targetProfile = this.profiles.profile(targetId);
    if (!targetProfile.notifyWhenFavorited()) {
      return;
    }

    var online = this.players.online(targetId);
    if (online.isEmpty()) {
      return;
    }

    var target = online.get();
    this.notifier.sendFavoritedNotification(target, ownerName);
  }
}
