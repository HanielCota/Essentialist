package com.hanielcota.essentials.modules.invsee.service;

import com.hanielcota.essentials.modules.invsee.domain.InvseeHolder;
import com.hanielcota.essentials.modules.invsee.domain.InvseeSnapshot;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Writes the editable slots of an /invsee view back into the target's real inventory (storage,
 * armor, offhand). Locked slots are intentionally skipped — they hold filler items only. Stateless.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class InvseeWriteback {

  public static boolean apply(
      @NonNull Player target, @NonNull InvseeHolder holder, @NonNull Inventory view) {
    var baseline = holder.snapshot();
    if (baseline == null || !baseline.matches(target)) {
      return false;
    }

    var next = InvseeSnapshot.fromView(view);
    next.writeTo(target);
    holder.snapshot(next);

    return true;
  }
}
