package com.hanielcota.essentials.modules.essentials.menu;

import com.hanielcota.essentials.menu.MenuLayouts;
import com.hanielcota.essentials.modules.essentials.config.ModulesMenuConfig;
import com.hanielcota.essentials.shared.Log;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.NonNull;

/**
 * Resolved, collision-free slot layout for the module-control menu, computed once when the menu is
 * registered.
 *
 * <p>The guide, filter button and the two page buttons all live at absolute slots. If an admin
 * configures any of them onto the same slot, the later control is bumped to the first free slot
 * (and a warning is logged) so no control is silently overwritten. Module items paginate through
 * whatever slots remain after the controls are reserved.
 */
public record ModulesMenuLayout(int infoSlot, int filterSlot, List<Integer> contentSlots) {

  private static final Log LOG = Log.of(ModulesMenuLayout.class);

  public static @NonNull ModulesMenuLayout resolve(@NonNull ModulesMenuConfig menu, int rows) {
    var slotCount = MenuLayouts.slotCount(rows);
    var navigation = menu.navigation();

    var occupied = new HashSet<Integer>();
    occupied.add(navigation.effectivePreviousSlot(rows));
    occupied.add(navigation.effectiveNextSlot(rows));

    var infoSlot = place("guide", menu.effectiveInfoSlot(rows), occupied, slotCount);
    var filterSlot = place("filter", menu.effectiveFilterSlot(rows), occupied, slotCount);

    var contentSlots = freeContentSlots(menu.effectiveContentSlots(), occupied);

    return new ModulesMenuLayout(infoSlot, filterSlot, contentSlots);
  }

  private static int place(
      @NonNull String control, int preferred, @NonNull Set<Integer> occupied, int slotCount) {
    if (occupied.add(preferred)) {
      return preferred;
    }

    var free = firstFreeSlot(occupied, slotCount);
    LOG.warn(
        "Module menu {} slot {} collides with another control; moved to {}",
        control,
        preferred,
        free);

    occupied.add(free);
    return free;
  }

  private static int firstFreeSlot(@NonNull Set<Integer> occupied, int slotCount) {
    for (var slot = 0; slot < slotCount; slot++) {
      if (!occupied.contains(slot)) {
        return slot;
      }
    }

    // Every slot is taken (degenerate config); stay in range rather than throw.
    return slotCount - 1;
  }

  private static List<Integer> freeContentSlots(
      @NonNull List<Integer> content, @NonNull Set<Integer> occupied) {
    var result = new ArrayList<Integer>(content.size());
    for (var slot : content) {
      if (occupied.contains(slot)) {
        continue;
      }
      result.add(slot);
    }

    return List.copyOf(result);
  }
}
