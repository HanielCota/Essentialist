package com.hanielcota.essentials.menu;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MenuLayouts {

  private static final int SLOTS_PER_ROW = 9;

  public static int clampRows(int rows) {
    return Math.clamp(rows, 1, 6);
  }

  public static int slotCount(int rows) {
    return clampRows(rows) * SLOTS_PER_ROW;
  }

  public static List<Integer> allSlots(int rows) {
    var slots = new ArrayList<Integer>(slotCount(rows));
    for (var slot = 0; slot < slotCount(rows); slot++) {
      slots.add(slot);
    }
    return slots;
  }

  public static List<Integer> fallbackContentSlots(int rows, int count) {
    var cappedCount = Math.clamp(count, 0, slotCount(rows));
    return List.copyOf(allSlots(rows).subList(0, cappedCount));
  }

  public static List<Integer> sanitizeSlots(@NonNull List<Integer> configured, int rows) {
    return sanitizeSlots(configured, rows, allSlots(rows));
  }

  public static List<Integer> sanitizeSlots(
      @NonNull List<Integer> configured, int rows, @NonNull List<Integer> fallback) {
    var maxSlot = slotCount(rows);
    var slots = new ArrayList<Integer>(configured.size());
    var seen = HashSet.<Integer>newHashSet(configured.size());
    for (var slot : configured) {
      if (slot == null || slot < 0 || slot >= maxSlot) {
        continue;
      }
      if (seen.add(slot)) {
        slots.add(slot);
      }
    }
    return slots.isEmpty() ? List.copyOf(fallback) : slots;
  }

  public static int sanitizeSlot(int configuredSlot, int rows, int fallbackSlot) {
    var maxSlot = slotCount(rows);
    if (configuredSlot >= 0 && configuredSlot < maxSlot) {
      return configuredSlot;
    }
    if (fallbackSlot >= 0 && fallbackSlot < maxSlot) {
      return fallbackSlot;
    }
    return Math.max(0, maxSlot - 1);
  }
}
