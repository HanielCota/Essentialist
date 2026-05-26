package com.hanielcota.essentials.modules.chat.placeholder;

import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * Identity resolver — returns the input unchanged. Used when no third-party placeholder plugin is
 * available, so callers can keep depending on {@link PlaceholderResolver} without null checks.
 */
public final class NoopPlaceholderResolver implements PlaceholderResolver {

  @Override
  public boolean isAvailable() {
    return false;
  }

  @Override
  public String apply(@NonNull Player sender, @NonNull String input) {
    return input;
  }
}
