package com.hanielcota.essentials.modules.warps.service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import lombok.NonNull;

/**
 * Validates {@code /setwarp} names against the configured length cap and character pattern. A warp
 * name flows into the {@code essentials.warp.use.<name>} permission node and the per-warp config
 * key, so unconstrained names are rejected at the boundary.
 */
public final class WarpNameValidator {

  // The pattern string comes from config and is stable across calls; compiling it per call would
  // re-parse the regex every /setwarp. Cache by source so a config reload still picks up changes.
  private final ConcurrentHashMap<String, Pattern> compiled = new ConcurrentHashMap<>();

  public boolean isValid(@NonNull String name, int maxLength, @NonNull String pattern) {
    if (name.isBlank() || name.length() > maxLength) {
      return false;
    }

    var regex = this.compiled.computeIfAbsent(pattern, Pattern::compile);
    var matcher = regex.matcher(name);

    return matcher.matches();
  }
}
