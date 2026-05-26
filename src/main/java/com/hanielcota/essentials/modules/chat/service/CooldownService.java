package com.hanielcota.essentials.modules.chat.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/**
 * Per-channel chat cooldown — purely in-memory, no Bukkit scheduler. Channel-agnostic: stores last
 * timestamps in a {@code Map<UUID, Map<String, Long>>} keyed by player and channel id, so adding a
 * new channel requires zero changes here.
 *
 * <p>Concurrency: the chat hot path runs on the async chat thread, while the cleanup listener fires
 * on the main thread. The outer {@link ConcurrentHashMap} handles visibility for the per- player
 * slot map; the inner {@link ConcurrentHashMap} handles per-channel writes. We accept that two
 * concurrent {@code touch} calls for the same player+channel may race their {@code
 * System.currentTimeMillis} writes, which at worst lets one extra message through during the same
 * millisecond — preferable to a synchronised section in the hot path.
 *
 * <p>Lazy expiry: cooldowns time out implicitly — {@link #remainingMillis} subtracts {@code now -
 * lastSend} from the configured cooldown. We never run a background sweep, so the map only shrinks
 * via the quit listener.
 */
public final class CooldownService {

  private final ConcurrentHashMap<UUID, Map<String, Long>> lastSendByPlayer =
      new ConcurrentHashMap<>();

  /**
   * Milliseconds the sender must still wait before another message on {@code channelId} is allowed.
   * Returns {@code 0} (or negative) when the cooldown is satisfied or when {@code cooldownSeconds
   * <= 0}.
   */
  public long remainingMillis(@NonNull UUID id, @NonNull String channelId, int cooldownSeconds) {
    if (cooldownSeconds <= 0) {
      return 0L;
    }

    var slots = this.lastSendByPlayer.get(id);
    if (slots == null) {
      return 0L;
    }

    var lastSend = slots.get(channelId);
    if (lastSend == null) {
      return 0L;
    }

    var cooldownMillis = (long) cooldownSeconds * 1_000L;
    var now = System.currentTimeMillis();
    var elapsed = now - lastSend;
    var remaining = cooldownMillis - elapsed;

    return Math.max(remaining, 0L);
  }

  public void touch(@NonNull UUID id, @NonNull String channelId) {
    var slots = this.lastSendByPlayer.computeIfAbsent(id, ignored -> new ConcurrentHashMap<>());
    var now = System.currentTimeMillis();

    slots.put(channelId, now);
  }

  public void clear(@NonNull UUID id) {
    this.lastSendByPlayer.remove(id);
  }
}
