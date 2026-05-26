package com.hanielcota.essentials.modules.chat.service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/**
 * Tracks the last plain-text message a player sent and answers {@link #isRepeat(UUID, String)}.
 * Comparison is exact (whitespace and case both matter). The check runs on the async chat thread;
 * the underlying {@link ConcurrentHashMap} handles the visibility without a {@code synchronized}
 * section.
 *
 * <p>Memory grows linearly with online unique players — entries are evicted by the quit listener
 * via {@link #clear(UUID)}. Storing only the last message (not history) caps the per-player
 * footprint at one String reference.
 */
public final class AntiSpamService {

  private final ConcurrentHashMap<UUID, String> lastMessage = new ConcurrentHashMap<>();

  public boolean isRepeat(@NonNull UUID id, @NonNull String message) {
    var previous = this.lastMessage.get(id);

    return message.equals(previous);
  }

  public void record(@NonNull UUID id, @NonNull String message) {
    this.lastMessage.put(id, message);
  }

  public void clear(@NonNull UUID id) {
    this.lastMessage.remove(id);
  }
}
