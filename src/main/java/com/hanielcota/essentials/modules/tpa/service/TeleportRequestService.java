package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.history.TpaHistory;
import com.hanielcota.essentials.modules.tpa.history.TpaHistoryEntry;
import com.hanielcota.essentials.modules.tpa.model.Destination;
import com.hanielcota.essentials.modules.tpa.model.Participant;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequestStatus;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequestType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Application service for the teleport-request use cases: create, accept, deny, cancel, expire.
 *
 * <p>Sole responsibility: orchestration. It owns no state and renders no messages â€” it delegates
 * storage to {@link RequestStore}, persistence to {@link TpaHistory} and player-facing notices to
 * {@link TpaNotifier}.
 */
@RequiredArgsConstructor
public final class TeleportRequestService {

  private final ConfigHandle<TpaConfig> config;
  private final RequestStore store;
  private final TpaHistory history;
  private final TpaNotifier notifier;

  /**
   * Registers a new request â€” replacing (and recording as cancelled) any request the requester
   * already had outstanding â€” and prompts the target.
   */
  public TeleportRequest create(
      @NonNull Player requester, @NonNull Player target, @NonNull TeleportRequestType type) {

    this.store
        .outgoingOf(requester.getUniqueId())
        .ifPresent(previous -> resolve(previous, TeleportRequestStatus.CANCELLED));

    var configSnapshot = this.config.value();
    var requestExpiryDuration = configSnapshot.requestExpiry();

    var request =
        TeleportRequest.open(
            Participant.of(requester), Participant.of(target), type, requestExpiryDuration);

    this.store.add(request);
    this.notifier.sendPrompt(target, request);
    return request;
  }

  /** The target's pending requests, newest first. */
  public List<TeleportRequest> incoming(@NonNull UUID target) {
    return this.store.incomingFor(target);
  }

  /** The requester's single outstanding request, if any. */
  public Optional<TeleportRequest> outgoing(@NonNull UUID requester) {
    return this.store.outgoingOf(requester);
  }

  /** A specific pending request to {@code target} from the named requester, case-insensitive. */
  public Optional<TeleportRequest> incomingFrom(
      @NonNull UUID target, @NonNull String requesterName) {
    return this.store.incomingFrom(target, requesterName);
  }

  /** Accepts a request: performs the teleport, then records the outcome. */
  public AcceptResult accept(@NonNull TeleportRequest request) {

    var requesterId = request.requester().id();
    var requester = Bukkit.getPlayer(requesterId);

    var targetId = request.target().id();
    var target = Bukkit.getPlayer(targetId);
    if (requester == null || target == null) {
      if (this.store.remove(request)) {
        this.history.push(TpaHistoryEntry.of(request, TeleportRequestStatus.CANCELLED));
      }
      return AcceptResult.REQUESTER_OFFLINE;
    }

    if (!this.store.remove(request)) {
      return AcceptResult.NOT_FOUND;
    }

    boolean toTarget = request.type() == TeleportRequestType.TPA;
    Player mover = toTarget ? requester : target;

    Player benchmark = toTarget ? target : requester;
    Location landing = benchmark.getLocation();

    if (!mover.teleport(landing)) {
      this.history.push(TpaHistoryEntry.of(request, TeleportRequestStatus.CANCELLED));
      return AcceptResult.TELEPORT_FAILED;
    }

    this.history.push(
        TpaHistoryEntry.of(request, TeleportRequestStatus.ACCEPTED, Destination.of(landing)));
    return AcceptResult.SUCCESS;
  }

  /** Denies a request. */
  public void deny(@NonNull TeleportRequest request) {
    resolve(request, TeleportRequestStatus.DENIED);
  }

  /** Cancels a request (the requester withdrew it). */
  public void cancel(@NonNull TeleportRequest request) {
    resolve(request, TeleportRequestStatus.CANCELLED);
  }

  /** Expires a request and notifies the requester. Called by {@link TeleportRequestExpiry}. */
  public void expire(@NonNull TeleportRequest request) {
    if (this.store.remove(request)) {
      this.history.push(TpaHistoryEntry.of(request, TeleportRequestStatus.EXPIRED));
      this.notifier.notifyExpired(request);
    }
  }

  /**
   * Cancels every request a player takes part in â€” used when they disconnect â€” and returns them
   * so the caller can notify the other party.
   */
  public List<TeleportRequest> cancelAllOf(@NonNull UUID player) {
    var affected = this.store.involving(player);
    for (TeleportRequest request : affected) {
      resolve(request, TeleportRequestStatus.CANCELLED);
    }
    return affected;
  }

  /** Removes a request and writes its terminal state to history in one step. */
  private void resolve(@NonNull TeleportRequest request, @NonNull TeleportRequestStatus status) {
    if (this.store.remove(request)) {
      this.history.push(TpaHistoryEntry.of(request, status));
    }
  }
}
