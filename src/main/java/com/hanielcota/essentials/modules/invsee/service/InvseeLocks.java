package com.hanielcota.essentials.modules.invsee.service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.NonNull;

/**
 * Single-viewer registry for /invsee. Pairs target → viewer so only one viewer can be looking at a
 * given target at a time. Without this lock, two viewers with snapshot-stale views would clobber
 * each other on writeback (item loss / dupe).
 */
public final class InvseeLocks {

  // target -> viewer.
  private final ConcurrentMap<UUID, UUID> activeByTarget = new ConcurrentHashMap<>();

  /** Returns {@code true} when the lock was acquired (no prior holder for this target). */
  public boolean tryAcquire(@NonNull UUID targetId, @NonNull UUID viewerId) {
    var existing = this.activeByTarget.putIfAbsent(targetId, viewerId);

    return existing == null;
  }

  /** Releases the lock on {@code targetId} only if held by {@code viewerId}. */
  public void release(@NonNull UUID targetId, @NonNull UUID viewerId) {
    this.activeByTarget.remove(targetId, viewerId);
  }

  /** Releases the lock on {@code targetId} unconditionally (target quit/died). */
  public void releaseTarget(@NonNull UUID targetId) {
    this.activeByTarget.remove(targetId);
  }
}
