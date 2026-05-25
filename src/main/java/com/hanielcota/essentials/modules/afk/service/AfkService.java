package com.hanielcota.essentials.modules.afk.service;

import com.hanielcota.essentials.modules.afk.domain.AfkState;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * In-memory AFK state + last-activity registry. State is per-session — dropped on quit and on
 * plugin disable. Activity timestamps are epoch millis to keep the auto checker arithmetic cheap.
 */
public final class AfkService {

  private final ConcurrentHashMap<UUID, AfkState> states = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<UUID, Long> lastActivityMillis = new ConcurrentHashMap<>();

  public Optional<AfkState> state(@NonNull UUID id) {
    var state = this.states.get(id);

    return Optional.ofNullable(state);
  }

  public boolean isAfk(@NonNull UUID id) {
    return this.states.containsKey(id);
  }

  /** Marks the player AFK. Returns {@code true} when this is a transition from non-AFK. */
  public boolean enter(@NonNull UUID id, @Nullable String reason) {
    var fresh = AfkState.withReason(reason);
    var previous = this.states.put(id, fresh);

    return previous == null;
  }

  /** Clears AFK state. Returns {@code true} when the player was AFK. */
  public boolean exit(@NonNull UUID id) {
    var previous = this.states.remove(id);

    return previous != null;
  }

  public void recordActivity(@NonNull UUID id, long epochMillis) {
    this.lastActivityMillis.put(id, epochMillis);
  }

  public long lastActivity(@NonNull UUID id, long fallback) {
    return this.lastActivityMillis.getOrDefault(id, fallback);
  }

  public void forget(@NonNull UUID id) {
    this.states.remove(id);
    this.lastActivityMillis.remove(id);
  }
}
