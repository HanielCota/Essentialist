package com.hanielcota.essentials.modules.homes.menu.presentation;

import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.modules.homes.config.menu.HomesMainMenuConfig;
import com.hanielcota.essentials.shared.Placeholders;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HomesMainMenuSection {

  private static final int MIN_ROWS = 1;

  public static int rows(@NonNull HomesMainMenuConfig snap) {
    return MenuLayouts.clampRows(snap.rows());
  }

  public static int infoSlot(@NonNull HomesMainMenuConfig snap) {
    return MenuLayouts.sanitizeSlot(snap.infoSlot(), rows(snap), 10);
  }

  public static int createSlot(@NonNull HomesMainMenuConfig snap) {
    return MenuLayouts.sanitizeSlot(snap.createSlot(), rows(snap), 16);
  }

  public static int sortSlot(@NonNull HomesMainMenuConfig snap) {
    return MenuLayouts.sanitizeSlot(snap.sortSlot(), rows(snap), 8);
  }

  public static List<Integer> contentSlots(@NonNull HomesMainMenuConfig snap) {
    var rows = rows(snap);
    var base = baseContentSlots(snap, rows);
    var reserved = reservedSlots(snap, rows);

    var result = new ArrayList<Integer>(base.size());
    for (var slot : base) {
      if (reserved.contains(slot)) {
        continue;
      }
      result.add(slot);
    }

    return List.copyOf(result);
  }

  private static List<Integer> baseContentSlots(@NonNull HomesMainMenuConfig snap, int rows) {
    var configured = snap.contentSlots();
    if (configured.isEmpty()) {
      var count = rows > MIN_ROWS ? (rows - 1) * 9 : 9;
      return MenuLayouts.fallbackContentSlots(rows, count);
    }

    return MenuLayouts.sanitizeSlots(configured, rows);
  }

  // The guide, sort, create and page buttons sit at fixed slots; the framework does not skip them
  // when distributing the paginated home items, so they are carved out here to stop a home landing
  // on top of a control (the create button shares the default content range otherwise).
  private static Set<Integer> reservedSlots(@NonNull HomesMainMenuConfig snap, int rows) {
    var navigation = snap.navigation();

    var reserved = new HashSet<Integer>();
    reserved.add(infoSlot(snap));
    reserved.add(sortSlot(snap));
    reserved.add(createSlot(snap));
    reserved.add(navigation.effectivePreviousSlot(rows));
    reserved.add(navigation.effectiveNextSlot(rows));

    return reserved;
  }

  public static String itemName(@NonNull HomesMainMenuConfig snap, @NonNull String name) {
    return snap.itemName().replace("{name}", name);
  }

  public static String[] itemLore(
      @NonNull HomesMainMenuConfig snap, @NonNull HomeMenuPlaceholders placeholders) {
    var template = snap.itemLore();
    var usage = snap.usageLore();
    var values = placeholderValues(placeholders);
    var rendered = new String[template.size() + usage.size()];

    for (var i = 0; i < template.size(); i++) {
      var line = template.get(i);
      rendered[i] = Placeholders.format(line, values);
    }
    for (var i = 0; i < usage.size(); i++) {
      var line = usage.get(i);
      rendered[template.size() + i] = Placeholders.format(line, values);
    }

    return rendered;
  }

  private static Map<String, String> placeholderValues(@NonNull HomeMenuPlaceholders ph) {
    return Map.ofEntries(
        Map.entry("world", ph.world()),
        Map.entry("x", ph.x()),
        Map.entry("y", ph.y()),
        Map.entry("z", ph.z()),
        Map.entry("direction", ph.direction()),
        Map.entry("created_date", ph.createdDate()),
        Map.entry("created_time", ph.createdTime()),
        Map.entry("created_at", ph.createdAt()),
        Map.entry("count", ph.count()),
        Map.entry("last_used", ph.lastUsed()));
  }
}
