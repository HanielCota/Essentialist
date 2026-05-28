package com.hanielcota.essentials.modules.tpa.service.selection;

import com.hanielcota.essentials.modules.tpa.domain.TpaContact;
import com.hanielcota.essentials.modules.tpa.domain.TpaPickPlayerFilter;
import com.hanielcota.essentials.modules.tpa.service.favorites.TpaFavoriteService;
import com.hanielcota.essentials.paper.PlayerProvider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Resolves the online-player list for {@code TpaPickPlayerMenu}, narrowing it down by the viewer's
 * current {@link TpaPickPlayerFilter}. Pure read-only — no state of its own.
 */
@RequiredArgsConstructor
public final class TpaPickPlayerCandidates {

  private final PlayerProvider players;
  private final TpaFavoriteService favorites;
  private final TpaContactService contacts;

  public List<Player> resolve(@NonNull Player viewer, @NonNull TpaPickPlayerFilter filter) {
    var viewerId = viewer.getUniqueId();
    var online = onlineOthers(viewerId);

    return switch (filter) {
      case ALL -> online;
      case FAVORITES -> keepFavorites(viewerId, online);
      case SAME_WORLD -> keepSameWorld(viewer, online);
      case RECENT -> keepRecentContacts(viewerId, online);
    };
  }

  private List<Player> onlineOthers(@NonNull UUID viewerId) {
    var all = this.players.all();
    var result = new ArrayList<Player>(all.size());
    for (var candidate : all) {
      if (candidate.getUniqueId().equals(viewerId)) {
        continue;
      }
      result.add(candidate);
    }
    return result;
  }

  private List<Player> keepFavorites(@NonNull UUID viewerId, @NonNull List<Player> online) {
    var favoriteIds = new HashSet<UUID>();
    for (var entry : this.favorites.favoritesOf(viewerId)) {
      favoriteIds.add(entry.favoriteId());
    }
    if (favoriteIds.isEmpty()) {
      return List.of();
    }

    var result = new ArrayList<Player>(online.size());
    for (var candidate : online) {
      if (favoriteIds.contains(candidate.getUniqueId())) {
        result.add(candidate);
      }
    }
    return result;
  }

  private static List<Player> keepSameWorld(@NonNull Player viewer, @NonNull List<Player> online) {
    var worldId = viewer.getWorld().getUID();
    var result = new ArrayList<Player>(online.size());
    for (var candidate : online) {
      if (candidate.getWorld().getUID().equals(worldId)) {
        result.add(candidate);
      }
    }
    return result;
  }

  private List<Player> keepRecentContacts(@NonNull UUID viewerId, @NonNull List<Player> online) {
    var contactIds = new HashSet<UUID>();
    for (TpaContact entry : this.contacts.top(viewerId, Integer.MAX_VALUE)) {
      contactIds.add(entry.targetId());
    }
    if (contactIds.isEmpty()) {
      return List.of();
    }

    var result = new ArrayList<Player>(online.size());
    for (var candidate : online) {
      if (contactIds.contains(candidate.getUniqueId())) {
        result.add(candidate);
      }
    }
    return result;
  }
}
