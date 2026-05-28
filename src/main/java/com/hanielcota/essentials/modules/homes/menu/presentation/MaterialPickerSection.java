package com.hanielcota.essentials.modules.homes.menu.presentation;

import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.modules.homes.config.menu.HomesPickerMenuConfig;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MaterialPickerSection {

  public static int rows(@NonNull HomesPickerMenuConfig snap) {
    return MenuLayouts.clampRows(snap.rows());
  }

  public static List<Integer> contentSlots(@NonNull HomesPickerMenuConfig snap) {
    return MenuLayouts.sanitizeSlots(snap.contentSlots(), rows(snap));
  }

  public static int backSlot(@NonNull HomesPickerMenuConfig snap) {
    var rowCount = rows(snap);

    return MenuLayouts.sanitizeSlot(snap.backSlot(), rowCount, rowCount * 9 - 5);
  }

  public static String itemName(@NonNull HomesPickerMenuConfig snap, @NonNull String material) {
    return snap.itemName().replace("{material}", material);
  }

  public static String[] itemLore(@NonNull HomesPickerMenuConfig snap, @NonNull String material) {
    var template = snap.itemLore();
    var rendered = new String[template.size()];
    for (var i = 0; i < template.size(); i++) {
      rendered[i] = template.get(i).replace("{material}", material);
    }

    return rendered;
  }

  public static String staticTitle(@NonNull HomesPickerMenuConfig snap) {
    var template = snap.title();
    if (template.contains("{name}")) {
      return "<dark_gray>Pick an icon";
    }

    return template;
  }
}
