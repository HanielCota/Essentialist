package com.hanielcota.essentials.modules.chat.placeholder;

import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * Resolves player-context placeholders inside a string. Abstraction over external integrations
 * (PlaceholderAPI, MiniPlaceholders, internal-only…) so the chat formatting pipeline never depends
 * on a concrete plugin.
 *
 * <p>Implementations must be thread-safe — the chat hot path runs on Paper's async chat thread, and
 * the resolver may be invoked from multiple callers concurrently.
 */
public interface PlaceholderResolver {

  /**
   * Returns {@code true} when the resolver actually transforms strings. Used by callers that want
   * to skip prefix/suffix lookups entirely when no resolver is wired.
   */
  boolean isAvailable();

  /**
   * Resolves every placeholder in {@code input} against {@code sender}'s context. Implementations
   * must return {@code input} unchanged when there is nothing to resolve — never throw.
   */
  String apply(@NonNull Player sender, @NonNull String input);
}
