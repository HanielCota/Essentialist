package com.hanielcota.essentials.modules.homes.menu.presentation;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.modules.homes.config.menu.HomesMenuConfig;
import com.hanielcota.essentials.modules.homes.domain.HomeOrdering;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

/**
 * Builds the /homes sort-cycle button: shows the current {@link HomeOrdering} and the full options
 * list with an active marker, mirroring the favorite-ordering renderer in the TPA module.
 */
public final class HomesSortRenderer {

  public ItemTemplate sortTemplate(
      @NonNull HomesMenuConfig settings, @NonNull HomeOrdering ordering) {
    var stateLabel = orderingLabel(settings, ordering);
    var name = settings.sortName().replace("{state}", stateLabel);
    var lore = renderLore(settings, stateLabel, ordering);

    return MenuTemplates.simple(settings.sortMaterial(), name, lore);
  }

  private static String orderingLabel(
      @NonNull HomesMenuConfig settings, @NonNull HomeOrdering ordering) {
    return switch (ordering) {
      case NAME -> settings.sortLabelName();
      case MOST_USED -> settings.sortLabelMostUsed();
      case RECENT -> settings.sortLabelRecent();
    };
  }

  private static List<String> renderLore(
      @NonNull HomesMenuConfig settings,
      @NonNull String stateLabel,
      @NonNull HomeOrdering ordering) {
    var lines = new ArrayList<String>(settings.sortLore().size() + 4);
    for (var template : settings.sortLore()) {
      if (template.contains("{options}")) {
        lines.addAll(options(settings, ordering));
        continue;
      }
      lines.add(template.replace("{state}", stateLabel));
    }
    return lines;
  }

  private static List<String> options(
      @NonNull HomesMenuConfig settings, @NonNull HomeOrdering current) {
    var marker = settings.sortActiveMarker();
    return List.of(
        markActive(settings.sortLabelName(), marker, current == HomeOrdering.NAME),
        markActive(settings.sortLabelMostUsed(), marker, current == HomeOrdering.MOST_USED),
        markActive(settings.sortLabelRecent(), marker, current == HomeOrdering.RECENT));
  }

  private static String markActive(@NonNull String label, @NonNull String marker, boolean active) {
    return active ? label + marker : label;
  }
}
