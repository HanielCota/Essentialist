package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.modules.tpa.model.RequestId;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

  /** Adds a request and indexes it by requester and by target. */
  public void add(TeleportRequest request) {
    Objects.requireNonNull(request, "request");

    byId.put(request.id(), request);
    outgoingByRequester.put(request.requester().id(), request.id());

    incomingByTarget
        .computeIfAbsent(request.target().id(), key -> ConcurrentHashMap.newKeySet())
        .add(request.id());
  }

  /** Removes a request. Returns {@code false} when it was already gone. */
  public boolean remove(TeleportRequest request) {
    Objects.requireNonNull(request, "request");

    if (byId.remove(request.id()) == null) {
      return false;
    }

    outgoingByRequester.remove(request.requester().id(), request.id());
    detachIncoming(request.target().id(), request.id());
    return true;
  }

  /** The target's pending requests, newest first. */
  public List<TeleportRequest> incomingFor(UUID target) {
    Objects.requireNonNull(target, "target");

    var ids = incomingByTarget.get(target);
    if (ids == null || ids.isEmpty()) {
      return List.of();
    }

    var requests = new ArrayList<TeleportRequest>(ids.size());
    for (var id : ids) {
      var request = byId.get(id);
      if (request != null) {
        requests.add(request);
      }
    }

    requests.sort(Comparator.comparing((TeleportRequest r) -> r.window().createdAt()).reversed());
    return List.copyOf(requests);
  }

  /** A pending request to {@code target} from the named requester, case-insensitive. */
  public Optional<TeleportRequest> incomingFrom(UUID target, String requesterName) {
    Objects.requireNonNull(target, "target");
    Objects.requireNonNull(requesterName, "requesterName");

    for (var request : incomingFor(target)) {
      if (request.requester().name().equalsIgnoreCase(requesterName)) {
        return Optional.of(request);
      }
    }

    return Optional.empty();
  }

  /** The requester's single outstanding request, if any. */
  public Optional<TeleportRequest> outgoingOf(UUID requester) {
    Objects.requireNonNull(requester, "requester");

    var id = outgoingByRequester.get(requester);
    if (id == null) {
      return Optional.empty();
    }

    return Optional.ofNullable(byId.get(id));
  }

  /** Every request whose window has lapsed by {@code now}. */
  public List<TeleportRequest> expiredAt(Instant now) {
    Objects.requireNonNull(now, "now");

    var expired = new ArrayList<TeleportRequest>();
    for (var request : byId.values()) {
      if (request.isExpired(now)) {
        expired.add(request);
      }
    }

    return List.copyOf(expired);
  }

  /** Every request the player takes part in, as requester or as target. */
  public List<TeleportRequest> involving(UUID player) {
    Objects.requireNonNull(player, "player");

    var involved = new ArrayList<TeleportRequest>();
    for (var request : byId.values()) {
      if (takesPart(request, player)) {
        involved.add(request);
      }
    }

    return List.copyOf(involved);
  }

  private void detachIncoming(UUID target, RequestId id) {
    var ids = incomingByTarget.get(target);
    if (ids == null) {
      return;
    }

    ids.remove(id);
    if (ids.isEmpty()) {
      incomingByTarget.remove(target, ids);
    }
  }

  private static boolean takesPart(TeleportRequest request, UUID player) {
    return request.requester().id().equals(player) || request.target().id().equals(player);
  }
}
