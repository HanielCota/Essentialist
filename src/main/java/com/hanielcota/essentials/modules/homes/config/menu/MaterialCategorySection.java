package com.hanielcota.essentials.modules.homes.config.menu;

import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.modules.homes.menu.presentation.MaterialCategory;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Layout + formatter helpers for the material category submenu (rows / content slots / back slot,
 * plus category display names and item formatters). Stateless — every call takes a config snapshot.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MaterialCategorySection {

  public static int rows(@NonNull HomesMenuConfig snap) {
    return MenuLayouts.clampRows(snap.categoryRows());
  }

  public static List<Integer> contentSlots(@NonNull HomesMenuConfig snap) {
    return MenuLayouts.sanitizeSlots(snap.categoryContentSlots(), rows(snap));
  }

  public static int backSlot(@NonNull HomesMenuConfig snap) {
    var rowCount = rows(snap);

    return MenuLayouts.sanitizeSlot(snap.categoryBackSlot(), rowCount, rowCount * 9 - 5);
  }

  public static String displayName(
      @NonNull HomesMenuConfig snap, @NonNull MaterialCategory category) {
    var configured = snap.categoryNames().get(category);
    if (configured != null) {
      return configured;
    }

    return category.name();
  }

  public static String itemName(@NonNull HomesMenuConfig snap, @NonNull String category) {
    return snap.categoryItemName().replace("{category}", category);
  }

  public static String[] itemLore(@NonNull HomesMenuConfig snap, @NonNull String category) {
    var template = snap.categoryItemLore();
    var rendered = new String[template.size()];
    for (var i = 0; i < template.size(); i++) {
      rendered[i] = template.get(i).replace("{category}", category);
    }

    return rendered;
  }
}
