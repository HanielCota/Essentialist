package com.hanielcota.essentials.modules.vanish.menu;

import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.modules.vanish.config.VanishConfig;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VanishMenuLayout {

  private static final int MIN_ROWS = 1;

  public static List<Integer> contentSlots(@NonNull VanishConfig snap) {
    var rows = snap.effectiveRows();
    var info = snap.effectiveInfoSlot();
    var sanitized = sanitizedSlots(snap, rows);

    if (!sanitized.contains(info)) {
      return sanitized;
    }

    return withoutInfoSlot(sanitized, info);
  }

  private static List<Integer> sanitizedSlots(@NonNull VanishConfig snap, int rows) {
    var configured = snap.menuContentSlots();
    if (configured.isEmpty()) {
      var count = rows > MIN_ROWS ? (rows - 1) * 9 : 9;
      return MenuLayouts.fallbackContentSlots(rows, count);
    }

    return MenuLayouts.sanitizeSlots(configured, rows);
  }

  private static List<Integer> withoutInfoSlot(@NonNull List<Integer> slots, int info) {
    var filtered = new ArrayList<Integer>(slots.size());
    for (var slot : slots) {
      if (slot == info) {
        continue;
      }
      filtered.add(slot);
    }

    return filtered;
  }
}
