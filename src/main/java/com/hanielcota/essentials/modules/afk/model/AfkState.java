package com.hanielcota.essentials.modules.afk.model;

import org.jspecify.annotations.Nullable;

/**
 * AFK marker. {@code reason == null} when the player went AFK without specifying a reason or was
 * flagged automatically by the idle checker.
 */
public record AfkState(@Nullable String reason) {

  public static AfkState withoutReason() {
    return new AfkState(null);
  }

  public static AfkState withReason(@Nullable String reason) {
    return new AfkState(reason);
  }
}
