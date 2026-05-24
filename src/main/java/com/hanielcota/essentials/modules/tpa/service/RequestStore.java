package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.modules.tpa.model.RequestId;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequest;
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
public final class RequestStore {

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
    this.incomingByTarget
        .computeIfAbsent(targetId, key -> ConcurrentHashMap.newKeySet())
        .add(requestId);

    this.outgoingByRequester.compute(
        requesterId,
        (key, previousId) -> {
          if (previousId != null && !previousId.equals(requestId)) {
            var previous = this.byId.remove(previousId);
            if (previous != null) {
              detachIncoming(previous.target().id(), previousId);
            }
          }
          return requestId;
        });
  }

  /** Removes a request. Returns {@code false} when it was already gone. */
  public boolean remove(@NonNull TeleportRequest request) {

    if (this.byId.remove(request.id()) == null) {
      return false;
    }

    this.outgoingByRequester.remove(request.requester().id(), request.id());
    detachIncoming(request.target().id(), request.id());
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

    requests.sort(
        Comparator.comparing(
                (TeleportRequest r) -> {
                  var window = r.window();
                  return window.createdAt();
                })
            .reversed());
    return List.copyOf(requests);
  }

  /** A pending request to {@code target} from the named requester, case-insensitive. */
  public Optional<TeleportRequest> incomingFrom(
      @NonNull UUID target, @NonNull String requesterName) {

    for (var request : incomingFor(target)) {
      if (request.requester().name().equalsIgnoreCase(requesterName)) {
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

    return Optional.ofNullable(this.byId.get(id));
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
