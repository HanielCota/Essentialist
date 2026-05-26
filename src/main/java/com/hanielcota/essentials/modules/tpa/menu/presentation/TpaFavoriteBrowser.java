package com.hanielcota.essentials.modules.tpa.menu.presentation;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.FavoriteOrdering;
import com.hanielcota.essentials.modules.tpa.domain.TpaContact;
import com.hanielcota.essentials.modules.tpa.domain.TpaFavorite;
import com.hanielcota.essentials.modules.tpa.service.TpaContactService;
import com.hanielcota.essentials.modules.tpa.service.TpaFavoriteService;
import com.hanielcota.essentials.paper.PlayerProvider;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class TpaFavoriteBrowser {

  private final @NonNull ConfigHandle<TpaConfig> config;
  private final @NonNull TpaFavoriteService favorites;
  private final @NonNull TpaContactService contacts;
  private final @NonNull PlayerProvider players;

  public BrowserView view(
      @NonNull UUID viewerId, @NonNull FavoriteOrdering ordering, int contentSize) {
    var favoriteEntries = sortedFavorites(viewerId, ordering);
    var reservedSlots = 1;
    var room = Math.max(0, contentSize - favoriteEntries.size() - reservedSlots);
    var suggestions = pickSuggestions(viewerId, favoriteEntries, room);

    return new BrowserView(favoriteEntries, suggestions);
  }

  private List<TpaFavorite> sortedFavorites(
      @NonNull UUID viewerId, @NonNull FavoriteOrdering ordering) {
    var entries = this.favorites.favoritesOf(viewerId);
    return switch (ordering) {
      case NAME -> entries;
      case RECENT -> sortByRecent(viewerId, entries);
      case ONLINE_FIRST -> sortByOnlineFirst(entries);
    };
  }

  private List<TpaFavorite> sortByRecent(
      @NonNull UUID viewerId, @NonNull List<TpaFavorite> entries) {
    var ownerContacts = this.contacts.top(viewerId, Integer.MAX_VALUE);
    var lastUsedByTarget = new HashMap<UUID, Long>(ownerContacts.size());
    for (var contact : ownerContacts) {
      lastUsedByTarget.put(contact.targetId(), contact.lastUsedAtEpochMs());
    }

    var copy = new ArrayList<>(entries);
    var recentComparator =
        Comparator.<TpaFavorite, Long>comparing(
                favorite -> lastUsedByTarget.getOrDefault(favorite.favoriteId(), 0L))
            .reversed();
    var nameComparator =
        Comparator.comparing(TpaFavorite::favoriteName, String.CASE_INSENSITIVE_ORDER);
    copy.sort(recentComparator.thenComparing(nameComparator));
    return copy;
  }

  private List<TpaFavorite> sortByOnlineFirst(@NonNull List<TpaFavorite> entries) {
    var copy = new ArrayList<>(entries);
    var onlineComparator =
        Comparator.<TpaFavorite, Boolean>comparing(
                favorite -> this.players.online(favorite.favoriteId()).isPresent())
            .reversed();
    var nameComparator =
        Comparator.comparing(TpaFavorite::favoriteName, String.CASE_INSENSITIVE_ORDER);
    copy.sort(onlineComparator.thenComparing(nameComparator));
    return copy;
  }

  private List<TpaContact> pickSuggestions(
      @NonNull UUID viewerId, @NonNull List<TpaFavorite> currentFavorites, int room) {
    var settings = this.config.value().favoritesMenu();
    var max = Math.max(0, settings.maxSuggestions());
    if (room <= 0 || max <= 0) {
      return List.of();
    }

    var favoriteIds = new HashSet<UUID>(currentFavorites.size());
    for (var favorite : currentFavorites) {
      favoriteIds.add(favorite.favoriteId());
    }

    var limit = Math.min(room, max);
    return pickFromContacts(viewerId, favoriteIds, limit);
  }

  private List<TpaContact> pickFromContacts(
      @NonNull UUID viewerId, @NonNull HashSet<UUID> excluded, int limit) {
    var contactsList = this.contacts.top(viewerId, Integer.MAX_VALUE);
    var picked = new ArrayList<TpaContact>(limit);
    for (var contact : contactsList) {
      if (excluded.contains(contact.targetId())) {
        continue;
      }
      picked.add(contact);
      if (picked.size() >= limit) {
        break;
      }
    }
    return picked;
  }

  public record BrowserView(List<TpaFavorite> favorites, List<TpaContact> suggestions) {

    public boolean isEmpty() {
      return this.favorites.isEmpty() && this.suggestions.isEmpty();
    }
  }
}
