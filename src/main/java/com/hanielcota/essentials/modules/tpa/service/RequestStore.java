package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.modules.tpa.model.TeleportRequest;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Storage port for the set of pending teleport requests.
 *
 * <p>The active requests live behind this abstraction so the orchestration layer never depends on a
 * concrete data structure — today {@link InMemoryRequestStore}, tomorrow potentially a proxy-wide
 * shared store.
 */
public interface RequestStore {

  /** Adds a request and indexes it by requester and by target. */
  void add(TeleportRequest request);

  /** Removes a request. Returns {@code false} when it was already gone. */
  boolean remove(TeleportRequest request);

  /** The target's pending requests, newest first. */
  List<TeleportRequest> incomingFor(UUID target);

  /** A pending request to {@code target} from the named requester, case-insensitive. */
  Optional<TeleportRequest> incomingFrom(UUID target, String requesterName);

  /** The requester's single outstanding request, if any. */
  Optional<TeleportRequest> outgoingOf(UUID requester);

  /** Every request whose window has lapsed by {@code now}. */
  List<TeleportRequest> expiredAt(Instant now);

  /** Every request the player takes part in, as requester or as target. */
  List<TeleportRequest> involving(UUID player);
}
