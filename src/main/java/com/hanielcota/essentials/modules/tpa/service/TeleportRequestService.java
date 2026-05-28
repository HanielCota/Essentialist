package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.AcceptOutcome;
import com.hanielcota.essentials.modules.tpa.domain.Participant;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestStatus;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.domain.TpaCreationResult;
import com.hanielcota.essentials.modules.tpa.repository.RequestRepository;
import com.hanielcota.essentials.paper.PlayerProvider;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

/**
 * Application service for the teleport-request use cases: create, accept, deny, cancel, expire.
 *
 * <p>Sole responsibility: lifecycle and storage orchestration. It owns no state and renders no
 * messages — it delegates storage to {@link RequestRepository} and bookkeeping to {@link
 * TpaRequestRecorder}. Player-facing notifications are the caller's responsibility.
 */
@RequiredArgsConstructor
public final class TeleportRequestService {

  private final @NonNull ConfigHandle<TpaConfig> config;
  private final @NonNull RequestRepository store;
  private final @NonNull PlayerProvider players;
  private final @NonNull TpaRequestPolicy policy;
  private final @NonNull TpaRequestRecorder recorder;
  private final @NonNull TeleportRequestExecutor executor;

  public Optional<TpaCreationResult> create(
      @NonNull Player requester, @NonNull Player target, @NonNull TeleportRequestType type) {
    return create(requester, target, type, true);
  }

  public Optional<TpaCreationResult> create(
      @NonNull Player requester,
      @NonNull Player target,
      @NonNull TeleportRequestType type,
      boolean notifyTarget) {
    var targetId = target.getUniqueId();
    var requesterId = requester.getUniqueId();
    var accepted = this.policy.canCreate(requester, target, type);
    if (!accepted) {
      return Optional.empty();
    }

    var existing = this.store.outgoingOf(requesterId);
    TeleportRequest replacedRequest = existing.orElse(null);

    if (replacedRequest != null) {
      this.store.delete(replacedRequest);
      this.recorder.recordTerminal(replacedRequest, TeleportRequestStatus.CANCELLED);
    }

    var snap = this.config.value();
    var lifetime = snap.requestExpiry();

    var requesterParticipant = Participant.of(requester);
    var targetParticipant = Participant.of(target);
    var request = TeleportRequest.open(requesterParticipant, targetParticipant, type, lifetime);

    this.store.add(request);
    this.recorder.recordCreated(requesterId, targetId);

    return Optional.of(new TpaCreationResult(request, replacedRequest));
  }

  /** The target's pending requests, newest first. */
  public List<TeleportRequest> incoming(@NonNull UUID target) {
    return this.store.incomingFor(target);
  }

  /** The requester's single outstanding request, if any. */
  public Optional<TeleportRequest> outgoing(@NonNull UUID requester) {
    return this.store.outgoingOf(requester);
  }

  public boolean isBlockedBy(@NonNull UUID blockerId, @NonNull UUID requesterId) {
    return this.policy.isBlockedBy(blockerId, requesterId);
  }

  public boolean isDndActive(@NonNull UUID targetId) {
    return this.policy.isDndActive(targetId);
  }

  public boolean isCrossWorldRefused(@NonNull Player requester, @NonNull Player target) {
    return this.policy.isCrossWorldRefused(requester, target);
  }

  /** A specific pending request to {@code target} from the named requester, case-insensitive. */
  public Optional<TeleportRequest> incomingFrom(
      @NonNull UUID target, @NonNull String requesterName) {
    return this.store.incomingFrom(target, requesterName);
  }

  public AcceptOutcome tryAccept(@NonNull TeleportRequest request) {
    if (!this.store.delete(request)) {
      return AcceptOutcome.NOT_FOUND;
    }

    var requesterId = request.requester().id();
    var requesterOnline = this.players.online(requesterId).isPresent();
    var targetId = request.target().id();
    var targetOnline = this.players.online(targetId).isPresent();

    if (!requesterOnline) {
      this.recorder.recordTerminal(request, TeleportRequestStatus.CANCELLED);
      return AcceptOutcome.REQUESTER_OFFLINE;
    }
    if (!targetOnline) {
      this.recorder.recordTerminal(request, TeleportRequestStatus.CANCELLED);
      return AcceptOutcome.TARGET_OFFLINE;
    }

    this.recorder.recordTerminal(request, TeleportRequestStatus.ACCEPTED);
    return AcceptOutcome.ACCEPTED;
  }

  public CompletableFuture<Boolean> dispatchTeleport(@NonNull TeleportRequest request) {
    var pending = this.executor.execute(request);
    return pending.thenApply(
        execution -> {
          if (!execution.succeeded()) {
            this.recorder.recordTeleportFailure(request);
            return false;
          }
          this.recorder.recordTeleportSuccess(request, execution);
          return true;
        });
  }

  /** Denies a request. Returns false when it was already resolved or expired. */
  public boolean deny(@NonNull TeleportRequest request) {
    return resolve(request, TeleportRequestStatus.DENIED);
  }

  /** Cancels a request (the requester withdrew it). Returns false when it was already resolved. */
  public boolean cancel(@NonNull TeleportRequest request) {
    return resolve(request, TeleportRequestStatus.CANCELLED);
  }

  /**
   * Expires a request. Returns true if the request was found and removed. The caller is responsible
   * for notifying the requester.
   */
  public boolean expire(@NonNull TeleportRequest request) {
    if (!this.store.delete(request)) {
      return false;
    }

    this.recorder.recordTerminal(request, TeleportRequestStatus.EXPIRED);
    return true;
  }

  /**
   * Cancels every request a player takes part in — used when they disconnect — and returns them so
   * the caller can notify the other party.
   */
  public List<TeleportRequest> cancelAllOf(@NonNull UUID player) {
    var candidates = this.store.involving(player);
    var cancelled = new java.util.ArrayList<TeleportRequest>();
    for (TeleportRequest request : candidates) {
      if (resolve(request, TeleportRequestStatus.CANCELLED)) {
        cancelled.add(request);
      }
    }
    return cancelled;
  }

  /** Removes a request and writes its terminal state to history in one step. */
  private boolean resolve(@NonNull TeleportRequest request, @NonNull TeleportRequestStatus status) {
    if (!this.store.delete(request)) {
      return false;
    }

    this.recorder.recordTerminal(request, status);
    return true;
  }
}
