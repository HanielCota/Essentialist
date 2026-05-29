package com.hanielcota.essentials.modules.kit.service;

import com.hanielcota.essentials.modules.kit.domain.KitClaimResult;
import lombok.NonNull;

/** Result of a claim attempt plus whether the inventory overflowed and items were dropped. */
public record ClaimOutcome(@NonNull KitClaimResult result, boolean overflowDropped) {

  public static ClaimOutcome of(@NonNull KitClaimResult result) {
    return new ClaimOutcome(result, false);
  }
}
