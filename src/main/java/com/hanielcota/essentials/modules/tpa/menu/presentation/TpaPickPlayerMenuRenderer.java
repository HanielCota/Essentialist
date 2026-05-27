package com.hanielcota.essentials.modules.tpa.menu.presentation;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.modules.tpa.config.menu.TpaPickPlayerMenuConfig;
import com.hanielcota.essentials.modules.tpa.domain.TpaPickPlayerFilter;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

public final class TpaPickPlayerMenuRenderer {

  public ItemTemplate filterTemplate(
      @NonNull TpaPickPlayerMenuConfig settings, @NonNull TpaPickPlayerFilter filter) {
    var stateLabel = filterLabel(settings, filter);
    var name = settings.filterName().replace("{filter}", stateLabel);
    var lore = renderLore(settings, stateLabel, filter);

    return MenuTemplates.simple(settings.filterIcon(), name, lore);
  }

  private static String filterLabel(
      @NonNull TpaPickPlayerMenuConfig settings, @NonNull TpaPickPlayerFilter filter) {
    return switch (filter) {
      case ALL -> settings.filterLabelAll();
      case FAVORITES -> settings.filterLabelFavorites();
      case SAME_WORLD -> settings.filterLabelSameWorld();
      case RECENT -> settings.filterLabelRecent();
    };
  }

  private static List<String> renderLore(
      @NonNull TpaPickPlayerMenuConfig settings,
      @NonNull String stateLabel,
      @NonNull TpaPickPlayerFilter filter) {
    var lines = new ArrayList<String>(settings.filterLore().size() + 4);
    for (var template : settings.filterLore()) {
      if (template.contains("{options}")) {
        lines.addAll(options(settings, filter));
        continue;
      }
      lines.add(template.replace("{filter}", stateLabel));
    }
    return lines;
  }

  private static List<String> options(
      @NonNull TpaPickPlayerMenuConfig settings, @NonNull TpaPickPlayerFilter current) {
    var marker = settings.filterActiveMarker();
    return List.of(
        markActive(settings.filterLabelAll(), marker, current == TpaPickPlayerFilter.ALL),
        markActive(
            settings.filterLabelFavorites(), marker, current == TpaPickPlayerFilter.FAVORITES),
        markActive(
            settings.filterLabelSameWorld(), marker, current == TpaPickPlayerFilter.SAME_WORLD),
        markActive(settings.filterLabelRecent(), marker, current == TpaPickPlayerFilter.RECENT));
  }

  private static String markActive(@NonNull String label, @NonNull String marker, boolean active) {
    return active ? label + marker : label;
  }
}
