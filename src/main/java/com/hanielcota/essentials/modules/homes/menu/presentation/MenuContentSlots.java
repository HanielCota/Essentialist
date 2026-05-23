package com.hanielcota.essentials.modules.homes.menu.presentation;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MenuContentSlots {

  private static final int SLOTS_PER_ROW = 9;

  public static List<Integer> allRows(int rows) {
    if (rows <= 0) {
      return List.of();
    }

    var capacity = rows * SLOTS_PER_ROW;
    var slots = new ArrayList<Integer>(capacity);

    for (var i = 0; i < capacity; i++) {
      slots.add(i);
    }

    return List.copyOf(slots);
  }
}
