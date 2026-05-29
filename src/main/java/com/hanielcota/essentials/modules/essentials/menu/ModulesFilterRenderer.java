package com.hanielcota.essentials.modules.essentials.menu;

import com.github.hanielcota.menuframework.definition.ItemTemplate;
import com.hanielcota.essentials.menu.MenuTemplates;
import com.hanielcota.essentials.modules.essentials.config.ModulesFilterConfig;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;

/**
 * Builds the module-menu category filter button: the current category plus the option list with an
 * active marker. Mirrors the warps filter button so every cycling filter looks the same.
 */
public final class ModulesFilterRenderer {

  private static final String OPTIONS_TOKEN = "{options}";
  private static final String STATE_TOKEN = "{state}";

  public @NonNull ItemTemplate render(
      @NonNull ModulesFilterConfig filter, @NonNull ModuleCategory current) {
    var stateLabel = filter.labelOf(current);
    var name = filter.name().replace(STATE_TOKEN, stateLabel);

    var lore = new ArrayList<String>(filter.lore().size() + ModuleCategory.values().length);
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
      @NonNull ModulesFilterConfig filter, @NonNull ModuleCategory current) {
    var lines = new ArrayList<String>(ModuleCategory.values().length);
    for (var category : ModuleCategory.values()) {
      var label = "<gray>" + filter.labelOf(category);
      if (category == current) {
        label += filter.activeMarker();
      }
      lines.add(label);
    }
    return lines;
  }
}
