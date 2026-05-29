package com.hanielcota.essentials.modules.kit.domain;

import lombok.NonNull;

/** Sort order for the kit list, cycled by the sort button. */
public enum KitSort {
  NAME,
  AVAILABLE;

  /** The next order in the cycle, wrapping back to the first. */
  public @NonNull KitSort next() {
    var all = values();
    var nextOrdinal = (ordinal() + 1) % all.length;

    return all[nextOrdinal];
  }
}
