package com.hanielcota.essentials.modules.homes.config.menu;

import com.hanielcota.essentials.menu.MenuLayouts;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Layout + formatter helpers for the material picker submenu (rows / content slots / back slot /
 * navigation, plus item formatters and a fallback title). Stateless — every call takes a config
 * snapshot.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MaterialPickerSection {

  public static int rows(@NonNull HomesMenuConfig snap) {
    return MenuLayouts.clampRows(snap.pickerRows());
  }

  public static List<Integer> contentSlots(@NonNull HomesMenuConfig snap) {
    return MenuLayouts.sanitizeSlots(snap.pickerContentSlots(), rows(snap));
  }

  public static int backSlot(@NonNull HomesMenuConfig snap) {
    var rowCount = rows(snap);

    return MenuLayouts.sanitizeSlot(snap.pickerBackSlot(), rowCount, rowCount * 9 - 5);
  }

  public static String itemName(@NonNull HomesMenuConfig snap, @NonNull String material) {
    return snap.pickerItemName().replace("{material}", material);
  }

  public static String[] itemLore(@NonNull HomesMenuConfig snap, @NonNull String material) {
    var template = snap.pickerItemLore();
    var rendered = new String[template.size()];
    for (var i = 0; i < template.size(); i++) {
      rendered[i] = template.get(i).replace("{material}", material);
    }

    return rendered;
  }

  /**
   * Title without a per-home {name} placeholder. Falls back to a static label when the template
   * still contains the placeholder so the picker submenu (which is rendered before a home is
   * selected) doesn't show a literal "{name}".
   */
  public static String staticTitle(@NonNull HomesMenuConfig snap) {
    var template = snap.pickerTitle();
    if (template.contains("{name}")) {
      return "<dark_gray>Pick an icon";
    }

    return template;
  }
}
