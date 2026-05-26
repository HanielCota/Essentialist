package com.hanielcota.essentials.modules.tpa.repository;

import com.hanielcota.essentials.modules.tpa.domain.RequestId;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;

/**
 * Thread-safe in-memory store of pending teleport requests.
 *
 * <p>{@code byId} is the source of truth; the two index maps are kept in sync with it so command
 * lookups never scan.
 */
public final class RequestRepository implements RequestStore {

  private static final Comparator<TeleportRequest> NEWEST_FIRST =
      Comparator.comparing(RequestRepository::createdAt).reversed();

  private final Map<RequestId, TeleportRequest> byId = new ConcurrentHashMap<>();
  private final Map<UUID, RequestId> outgoingByRequester = new ConcurrentHashMap<>();
  private final Map<UUID, Set<RequestId>> incomingByTarget = new ConcurrentHashMap<>();

  private static boolean takesPart(@NonNull TeleportRequest request, @NonNull UUID player) {
    var requesterId = request.requester().id();
    var isRequester = requesterId.equals(player);

    var targetId = request.target().id();
    var isTarget = targetId.equals(player);

    return isRequester || isTarget;
  }

  private static Instant createdAt(@NonNull TeleportRequest request) {
    var window = request.window();
    return window.createdAt();
  }

  private static Set<RequestId> newRequestIdSet() {
    return ConcurrentHashMap.newKeySet();
  }

  /**
   * Adds a request and indexes it by requester and by target.
   *
   * <p>If the requester already had an outgoing request indexed (caller raced or forgot to resolve
   * it first), the prior request's row is purged from {@link #byId} and {@link #incomingByTarget}
   * before the new index is installed — otherwise the previous request would stay reachable to the
   * target's {@code /tpaccept} long after the indexes pointed elsewhere. The {@code
   * outgoingByRequester} mutation goes through {@code compute} so two concurrent {@code add} calls
   * for the same requester cannot leave a dangling index entry pointing at an already-removed
   * request id.
   */
  public void add(@NonNull TeleportRequest request) {
    var requestId = request.id();
    var requesterId = request.requester().id();
    var targetId = request.target().id();

    this.byId.put(requestId, request);

    var incomingIds = this.incomingByTarget.computeIfAbsent(targetId, key -> newRequestIdSet());
    incomingIds.add(requestId);

    this.outgoingByRequester.compute(
        requesterId, (key, previousId) -> swapOutgoing(previousId, requestId));
  }

  /** Removes a request. Returns {@code false} when it was already gone. */
  public boolean remove(@NonNull TeleportRequest request) {
    var requestId = request.id();
    if (this.byId.remove(requestId) == null) {
      return false;
    }

    var requesterId = request.requester().id();
    this.outgoingByRequester.remove(requesterId, requestId);

    var targetId = request.target().id();
    detachIncoming(targetId, requestId);

    return true;
  }

  /** The target's pending requests, newest first. */
  public List<TeleportRequest> incomingFor(@NonNull UUID target) {
    var ids = this.incomingByTarget.get(target);
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }

    var requests = new ArrayList<TeleportRequest>(ids.size());
    for (var id : ids) {
      var request = this.byId.get(id);
      if (request != null) {
        requests.add(request);
      }
    }

    requests.sort(NEWEST_FIRST);
    return List.copyOf(requests);
  }

  /** A pending request to {@code target} from the named requester, case-insensitive. */
  public Optional<TeleportRequest> incomingFrom(
      @NonNull UUID target, @NonNull String requesterName) {
    var pending = incomingFor(target);

    for (var request : pending) {
      var name = request.requester().name();
      if (name.equalsIgnoreCase(requesterName)) {
        return Optional.of(request);
      }
    }

    return Optional.empty();
  }

  /** The requester's single outstanding request, if any. */
  public Optional<TeleportRequest> outgoingOf(@NonNull UUID requester) {
    var id = this.outgoingByRequester.get(requester);
    if (id == null) {
      return Optional.empty();
    }

    var request = this.byId.get(id);
    return Optional.ofNullable(request);
  }

  /** Every request whose window has lapsed by {@code now}. */
  public List<TeleportRequest> expiredAt(@NonNull Instant now) {
    var expired = new ArrayList<TeleportRequest>();
    for (var request : this.byId.values()) {
      if (request.isExpired(now)) {
        expired.add(request);
      }
    }

    return List.copyOf(expired);
  }

  /** Every request the player takes part in, as requester or as target. */
  public List<TeleportRequest> involving(@NonNull UUID player) {
    var involved = new ArrayList<TeleportRequest>();
    for (var request : this.byId.values()) {
      if (takesPart(request, player)) {
        involved.add(request);
      }
    }

    return List.copyOf(involved);
  }

  private RequestId swapOutgoing(RequestId previousId, @NonNull RequestId newId) {
    if (previousId == null || previousId.equals(newId)) {
      return newId;
    }

    var previous = this.byId.remove(previousId);
    if (previous != null) {
      var previousTargetId = previous.target().id();
      detachIncoming(previousTargetId, previousId);
    }

    return newId;
  }

  private void detachIncoming(@NonNull UUID target, @NonNull RequestId id) {
    var ids = this.incomingByTarget.get(target);
    if (ids == null) {
      return;
    }

    ids.remove(id);
    if (ids.isEmpty()) {
      this.incomingByTarget.remove(target, ids);
    }
  }
}
