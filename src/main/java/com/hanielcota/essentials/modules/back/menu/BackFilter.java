package com.hanielcota.essentials.modules.back.menu;

import com.hanielcota.essentials.menu.ListMarkers;
import com.hanielcota.essentials.modules.back.config.BackConfig;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.Cause;
import com.hanielcota.essentials.modules.teleport.history.TeleportHistory.HistoryEntry;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BackFilter {

  public static List<HistoryEntry> apply(
      @NonNull List<HistoryEntry> entries, @Nullable Cause filter) {
    if (filter == null) {
      return entries;
    }

    var filtered = new ArrayList<HistoryEntry>(entries.size());
    for (var entry : entries) {
      if (entry.cause() == filter) {
        filtered.add(entry);
      }
    }
    return filtered;
  }

  public static String filterLabel(@NonNull BackConfig snap, @Nullable Cause filter) {
    if (filter == null) {
      return snap.filterAll();
    }
    return snap.causeLabel(filter);
  }

  public static List<String> filterOptions(@NonNull BackConfig snap, @Nullable Cause current) {
    var marker = snap.filterActiveMarker();
    var deathLabel = snap.causeLabel(Cause.DEATH);
    var teleportLabel = snap.causeLabel(Cause.TELEPORT);

    var all = ListMarkers.markActive(snap.filterAll(), marker, current == null);
    var death = ListMarkers.markActive(deathLabel, marker, current == Cause.DEATH);
    var teleport = ListMarkers.markActive(teleportLabel, marker, current == Cause.TELEPORT);

    return List.of(all, death, teleport);
  }

  public static List<String> renderFilterLore(
      @NonNull BackConfig snap, @NonNull String filterLabel, @Nullable Cause filter) {
    var template = snap.filterLore();
    var lines = new ArrayList<String>(template.size() + 2);

    for (var line : template) {
      if (line.contains("{options}")) {
        lines.addAll(filterOptions(snap, filter));
        continue;
      }
      var rendered = line.replace("{filter}", filterLabel);
      lines.add(rendered);
    }

    return lines;
  }
}
