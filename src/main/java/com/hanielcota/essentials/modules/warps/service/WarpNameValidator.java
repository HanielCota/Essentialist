package com.hanielcota.essentials.modules.warps.service;

import java.util.regex.Pattern;
import lombok.NonNull;

/**
 * Validates {@code /setwarp} names against the configured length cap and character pattern. A warp
 * name flows into the {@code essentials.warp.use.<name>} permission node and the per-warp config
 * key, so unconstrained names are rejected at the boundary.
 */
public final class WarpNameValidator {

  public boolean isValid(@NonNull String name, int maxLength, @NonNull String pattern) {
    if (name.isBlank() || name.length() > maxLength) {
      return false;
    }

    return Pattern.matches(pattern, name);
  }
}
