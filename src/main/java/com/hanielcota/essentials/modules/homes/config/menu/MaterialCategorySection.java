package com.hanielcota.essentials.modules.homes.config.menu;

import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialCategory;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MaterialCategorySection {

  public static int rows(@NonNull HomesCategoryMenuConfig snap) {
    return MenuLayouts.clampRows(snap.rows());
  }

  public static List<Integer> contentSlots(@NonNull HomesCategoryMenuConfig snap) {
    return MenuLayouts.sanitizeSlots(snap.contentSlots(), rows(snap));
  }

  public static int backSlot(@NonNull HomesCategoryMenuConfig snap) {
    var rowCount = rows(snap);

    return MenuLayouts.sanitizeSlot(snap.backSlot(), rowCount, rowCount * 9 - 5);
  }

  public static String displayName(
      @NonNull HomesCategoryMenuConfig snap, @NonNull MaterialCategory category) {
    var configured = snap.names().get(category);
    if (configured != null) {
      return configured;
    }

    return category.name();
  }

  public static String itemName(@NonNull HomesCategoryMenuConfig snap, @NonNull String category) {
    return snap.itemName().replace("{category}", category);
  }

  public static String[] itemLore(@NonNull HomesCategoryMenuConfig snap, @NonNull String category) {
    var template = snap.itemLore();
    var rendered = new String[template.size()];
    for (var i = 0; i < template.size(); i++) {
      rendered[i] = template.get(i).replace("{category}", category);
    }

    return rendered;
  }
}
