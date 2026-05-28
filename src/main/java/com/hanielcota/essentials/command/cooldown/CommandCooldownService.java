package com.hanielcota.essentials.command.cooldown;

import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/**
 * Tracks per-(player, command) cooldown timestamps for {@link CooldownInterceptor}. Replaces the
 * framework's annotation-bound cooldown so durations can come from config. Keyed by actor id +
 * command root; command dispatch is single-threaded per player, but the map is concurrent for
 * safety.
 */
public final class CommandCooldownService {

  private final ConcurrentHashMap<String, Long> lastUsed = new ConcurrentHashMap<>();

  private static String key(@NonNull String actorId, @NonNull String command) {
    return actorId + ':' + command;
  }

  /** Milliseconds left on the cooldown, or 0 when the command may run. */
  public long remainingMillis(
      @NonNull String actorId, @NonNull String command, long durationMillis, long now) {
    if (durationMillis <= 0) {
      return 0;
    }

    var last = this.lastUsed.get(key(actorId, command));
    if (last == null) {
      return 0;
    }

    var elapsed = now - last;
    var remaining = durationMillis - elapsed;

    return Math.max(0, remaining);
  }

  public void record(@NonNull String actorId, @NonNull String command, long now) {
    var mapKey = key(actorId, command);
    this.lastUsed.put(mapKey, now);
  }
}
