package com.hanielcota.essentials.modules.kit.domain;

/** Outcome of a kit-claim attempt, mapped to a message by the claim flow. */
public enum KitClaimResult {
  CLAIMED,
  ON_COOLDOWN,
  ALREADY_CLAIMED,
  NO_PERMISSION,
  EMPTY,
  UNKNOWN_KIT
}
