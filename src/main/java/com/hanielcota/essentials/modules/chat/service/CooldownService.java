package com.hanielcota.essentials.modules.chat.service;

import com.hanielcota.essentials.modules.chat.channel.GlobalChannel;
import com.hanielcota.essentials.modules.chat.channel.LocalChannel;
import com.hanielcota.essentials.modules.chat.channel.StaffChannel;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/**
 * Per-channel chat cooldown — purely in-memory, no Bukkit scheduler. State is a {@link
 * ConcurrentHashMap} from player UUID to a {@code long[3]} of "last-send" millis; the indices
 * correspond 1:1 to the three known channels via {@link #indexOf(String)}. The {@code long[]} is
 * tiny (≈ 40 bytes incl. header) and lets us read/write without per-channel map lookups.
 *
 * <p>Concurrency: the chat hot path runs on the async chat thread, while the cleanup listener fires
 * on the main thread. {@code ConcurrentHashMap.computeIfAbsent}/{@code get} are the only writes; we
 * accept that two concurrent {@code touch} calls for the same player may race their {@code System
 * .currentTimeMillis} writes, which at worst lets one extra message through during the same
 * millisecond — preferable to a synchronised section in the hot path.
 *
 * <p>Lazy expiry: cooldowns time out implicitly — {@link #remainingMillis} subtracts {@code now -
 * lastSend} from the configured cooldown. We never run a background sweep, so the map only shrinks
 * via the quit listener.
 */
public final class CooldownService {

  private static final int CHANNEL_COUNT = 3;
  private static final int GLOBAL_INDEX = 0;
  private static final int LOCAL_INDEX = 1;
  private static final int STAFF_INDEX = 2;

  private final ConcurrentHashMap<UUID, long[]> lastSendByPlayer = new ConcurrentHashMap<>();

  /**
   * Milliseconds the sender must still wait before another message on {@code channelId} is allowed.
   * Returns {@code 0} (or negative) when the cooldown is satisfied or {@code cooldownSeconds <= 0}.
   */
  public long remainingMillis(@NonNull UUID id, @NonNull String channelId, int cooldownSeconds) {
    if (cooldownSeconds <= 0) {
      return 0L;
    }

    var slots = this.lastSendByPlayer.get(id);
    if (slots == null) {
      return 0L;
    }

    var index = indexOf(channelId);
    var lastSend = slots[index];
    if (lastSend == 0L) {
      return 0L;
    }

    var cooldownMillis = (long) cooldownSeconds * 1_000L;
    var now = System.currentTimeMillis();
    var elapsed = now - lastSend;
    var remaining = cooldownMillis - elapsed;

    return Math.max(remaining, 0L);
  }

  public void touch(@NonNull UUID id, @NonNull String channelId) {
    var slots = this.lastSendByPlayer.computeIfAbsent(id, ignored -> new long[CHANNEL_COUNT]);
    var index = indexOf(channelId);

    slots[index] = System.currentTimeMillis();
  }

  public void clear(@NonNull UUID id) {
    this.lastSendByPlayer.remove(id);
  }

  private static int indexOf(@NonNull String channelId) {
    return switch (channelId) {
      case GlobalChannel.ID -> GLOBAL_INDEX;
      case LocalChannel.ID -> LOCAL_INDEX;
      case StaffChannel.ID -> STAFF_INDEX;
      default -> throw new IllegalArgumentException("Unknown channel id: " + channelId);
    };
  }
}
