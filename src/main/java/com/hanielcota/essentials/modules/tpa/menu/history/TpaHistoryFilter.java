package com.hanielcota.essentials.modules.tpa.menu.history;

import com.hanielcota.essentials.menu.ListMarkers;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestStatus;
import com.hanielcota.essentials.modules.tpa.history.TpaHistoryEntry;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TpaHistoryFilter {

  public static List<TpaHistoryEntry> apply(
      @NonNull List<TpaHistoryEntry> entries, @Nullable TeleportRequestStatus filter) {
    if (filter == null) {
      return entries;
    }
    var filtered = new ArrayList<TpaHistoryEntry>(entries.size());
    for (var entry : entries) {
      if (entry.status() == filter) {
        filtered.add(entry);
      }
    }
    return filtered;
  }

  public static List<String> filterOptions(
      @NonNull TpaMenuConfig settings, @Nullable TeleportRequestStatus current) {
    var marker = settings.filterActiveMarker();
    return List.of(
        ListMarkers.markActive(settings.filterAll(), marker, current == null),
        ListMarkers.markActive(
            settings.statusAccepted(), marker, current == TeleportRequestStatus.ACCEPTED),
        ListMarkers.markActive(
            settings.statusDenied(), marker, current == TeleportRequestStatus.DENIED),
        ListMarkers.markActive(
            settings.statusExpired(), marker, current == TeleportRequestStatus.EXPIRED),
        ListMarkers.markActive(
            settings.statusCancelled(), marker, current == TeleportRequestStatus.CANCELLED));
  }

  public static List<String> renderFilterLore(
      @NonNull TpaMenuConfig settings,
      @NonNull String filterLabel,
      @Nullable TeleportRequestStatus filter) {
    var lines = new ArrayList<String>(settings.filterLore().size() + 4);
    for (var template : settings.filterLore()) {
      if (template.contains("{options}")) {
        lines.addAll(filterOptions(settings, filter));
        continue;
      }
      lines.add(template.replace("{filter}", filterLabel));
    }
    return lines;
  }
}
