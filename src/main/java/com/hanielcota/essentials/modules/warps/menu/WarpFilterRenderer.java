package com.hanielcota.essentials.modules.warps.menu;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.modules.warps.config.WarpsFilterConfig;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

/**
 * Builds the warps-menu filter button: the current state plus the option list with an active
 * marker.
 */
public final class WarpFilterRenderer {

  private static final String OPTIONS_TOKEN = "{options}";
  private static final String STATE_TOKEN = "{state}";

  public @NonNull ItemTemplate render(
      @NonNull WarpsFilterConfig filter, @NonNull WarpFilter current) {
    var stateLabel = labelOf(filter, current);
    var name = filter.name().replace(STATE_TOKEN, stateLabel);

    var lore = new ArrayList<String>(filter.lore().size() + WarpFilter.values().length);
    for (var line : filter.lore()) {
      if (line.equals(OPTIONS_TOKEN)) {
        lore.addAll(options(filter, current));
        continue;
      }
      lore.add(line.replace(STATE_TOKEN, stateLabel));
    }

    return MenuTemplates.simple(filter.material(), name, lore);
  }

  private static List<String> options(
      @NonNull WarpsFilterConfig filter, @NonNull WarpFilter current) {
    var lines = new ArrayList<String>(WarpFilter.values().length);
    for (var mode : WarpFilter.values()) {
      var label = "<gray>" + labelOf(filter, mode);
      if (mode == current) {
        label += filter.activeMarker();
      }
      lines.add(label);
    }
    return lines;
  }

  private static String labelOf(@NonNull WarpsFilterConfig filter, @NonNull WarpFilter mode) {
    return switch (mode) {
      case DEFAULT -> filter.labelDefault();
      case MOST_PLAYERS -> filter.labelMostPlayers();
      case LEAST_PLAYERS -> filter.labelLeastPlayers();
      case MOST_LIKED -> filter.labelMostLiked();
      case FAVORITES -> filter.labelFavorites();
      case PVP -> filter.labelPvp();
    };
  }
}
