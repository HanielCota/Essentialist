package com.hanielcota.essentials.modules.kit.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.kit.config.KitConfig;
import com.hanielcota.essentials.modules.kit.domain.Kit;
import com.hanielcota.essentials.modules.kit.domain.KitClaimResult;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Validates a claim (permission, one-time, cooldown), gives the items, records usage and feedback.
 */
@RequiredArgsConstructor
public final class KitClaimService {

  /** Lets a player skip the one-time and cooldown gates (for staff / VIP). */
  public static final String BYPASS_PERMISSION = "essentials.kit.bypass";

  private final ConfigHandle<KitConfig> config;
  private final KitCooldownService cooldowns;
  private final KitGiver giver;

  /** A player claiming a kit themselves: enforces permission, one-time and cooldown. */
  public ClaimOutcome claim(@NonNull Player player, @NonNull Kit kit) {
    var uuid = player.getUniqueId();
    var bypass = player.hasPermission(BYPASS_PERMISSION);

    if (kit.hasPermission() && !player.hasPermission(kit.permission())) {
      return ClaimOutcome.of(KitClaimResult.NO_PERMISSION);
    }
    if (kit.isEmpty()) {
      return ClaimOutcome.of(KitClaimResult.EMPTY);
    }
    if (kit.oneTime() && !bypass && this.cooldowns.hasClaimed(uuid, kit)) {
      return ClaimOutcome.of(KitClaimResult.ALREADY_CLAIMED);
    }
    if (kit.hasCooldownGate() && !bypass && this.cooldowns.remainingSeconds(uuid, kit) > 0) {
      return ClaimOutcome.of(KitClaimResult.ON_COOLDOWN);
    }

    var outcome = deliverItems(player, kit);
    if (outcome.result() == KitClaimResult.CLAIMED) {
      this.cooldowns.markClaimed(uuid, kit);
    }

    return outcome;
  }

  /**
   * An admin handing a kit to {@code target}: no permission, one-time or cooldown checks, and the
   * target's cooldown is not started.
   */
  public ClaimOutcome deliver(@NonNull Player target, @NonNull Kit kit) {
    if (kit.isEmpty()) {
      return ClaimOutcome.of(KitClaimResult.EMPTY);
    }

    return deliverItems(target, kit);
  }

  private ClaimOutcome deliverItems(@NonNull Player player, @NonNull Kit kit) {
    var snap = this.config.value();
    var giveResult = this.giver.give(player, kit, snap.dropWhenInventoryFull());
    if (giveResult == KitGiver.GiveResult.REJECTED_FULL) {
      return ClaimOutcome.of(KitClaimResult.INVENTORY_FULL);
    }

    playClaimSound(player, snap);

    var overflowDropped = giveResult == KitGiver.GiveResult.OVERFLOW_DROPPED;
    return new ClaimOutcome(KitClaimResult.CLAIMED, overflowDropped);
  }

  private static void playClaimSound(@NonNull Player player, @NonNull KitConfig snap) {
    if (!snap.playsClaimSound()) {
      return;
    }

    var location = player.getLocation();
    player.playSound(location, snap.claimSound(), snap.claimVolume(), snap.claimPitch());
  }
}
