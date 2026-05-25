package com.hanielcota.essentials.modules.homes.config.menu;

import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.util.Numbers;
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
      @NonNull HomesMenuConfig snap, @NonNull String world, double x, double y, double z) {
    var xStr = Numbers.compact(x);
    var yStr = Numbers.compact(y);
    var zStr = Numbers.compact(z);
    var template = snap.itemLore();
    var rendered = new String[template.size()];

    for (var i = 0; i < template.size(); i++) {
      var line = template.get(i);
      var withWorld = line.replace("{world}", world);
      var withX = withWorld.replace("{x}", xStr);
      var withY = withX.replace("{y}", yStr);
      rendered[i] = withY.replace("{z}", zStr);
    }

    return rendered;
  }
}
