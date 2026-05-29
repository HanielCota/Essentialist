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

  private final ConfigHandle<KitConfig> config;
  private final KitCooldownService cooldowns;
  private final KitGiver giver;

  public ClaimOutcome claim(@NonNull Player player, @NonNull Kit kit) {
    var uuid = player.getUniqueId();

    if (kit.hasPermission() && !player.hasPermission(kit.permission())) {
      return ClaimOutcome.of(KitClaimResult.NO_PERMISSION);
    }
    if (kit.items().isEmpty()) {
      return ClaimOutcome.of(KitClaimResult.EMPTY);
    }
    if (kit.oneTime() && this.cooldowns.hasClaimed(uuid, kit)) {
      return ClaimOutcome.of(KitClaimResult.ALREADY_CLAIMED);
    }
    if (kit.hasCooldown() && this.cooldowns.remainingSeconds(uuid, kit) > 0) {
      return ClaimOutcome.of(KitClaimResult.ON_COOLDOWN);
    }

    var snap = this.config.value();
    var giveResult = this.giver.give(player, kit, snap.dropWhenInventoryFull());
    if (giveResult == KitGiver.GiveResult.REJECTED_FULL) {
      return ClaimOutcome.of(KitClaimResult.INVENTORY_FULL);
    }

    this.cooldowns.markClaimed(uuid, kit);
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
