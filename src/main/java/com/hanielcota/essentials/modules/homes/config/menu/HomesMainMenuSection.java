package com.hanielcota.essentials.modules.homes.config.menu;

import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.modules.homes.menu.presentation.HomeMenuPlaceholders;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * Layout + formatter helpers for the /homes main menu (rows / content slots / info slot, plus
 * per-home item name/lore rendering). Stateless — every call takes a config snapshot.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HomesMainMenuSection {

  private static final int MIN_ROWS = 1;

  public static int rows(@NonNull HomesMenuConfig snap) {
    return MenuLayouts.clampRows(snap.rows());
  }

  public static int infoSlot(@NonNull HomesMenuConfig snap) {
    return MenuLayouts.sanitizeSlot(snap.infoSlot(), rows(snap), 10);
  }

  public static List<Integer> contentSlots(@NonNull HomesMenuConfig snap) {
    var configured = snap.contentSlots();
    if (configured.isEmpty()) {
      var effRows = rows(snap);
      var count = effRows > MIN_ROWS ? (effRows - 1) * 9 : 9;
      return MenuLayouts.fallbackContentSlots(effRows, count);
    }

    return MenuLayouts.sanitizeSlots(configured, rows(snap));
  }

  public static String itemName(@NonNull HomesMenuConfig snap, @NonNull String name) {
    return snap.itemName().replace("{name}", name);
  }

  public static String[] itemLore(
      @NonNull HomesMenuConfig snap, @NonNull HomeMenuPlaceholders placeholders) {
    var template = snap.itemLore();
    var rendered = new String[template.size()];

    for (var i = 0; i < template.size(); i++) {
      var line = template.get(i);
      rendered[i] = formatLine(line, placeholders);
    }

    return rendered;
  }

  private static String formatLine(
      @NonNull String line, @NonNull HomeMenuPlaceholders placeholders) {
    var withWorld = line.replace("{world}", placeholders.world());
    var withX = withWorld.replace("{x}", placeholders.x());
    var withY = withX.replace("{y}", placeholders.y());
    var withZ = withY.replace("{z}", placeholders.z());
    var withDirection = withZ.replace("{direction}", placeholders.direction());
    var withDate = withDirection.replace("{created_date}", placeholders.createdDate());
    var withTime = withDate.replace("{created_time}", placeholders.createdTime());

    return withTime.replace("{created_at}", placeholders.createdAt());
  }
}
