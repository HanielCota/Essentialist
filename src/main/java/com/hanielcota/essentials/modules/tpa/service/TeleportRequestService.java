package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.config.ConfigHandle;
import com.hanielcota.essentials.modules.tpa.command.TpaNotifier;
import com.hanielcota.essentials.modules.tpa.config.TpaConfig;
import com.hanielcota.essentials.modules.tpa.domain.Participant;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestStatus;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import com.hanielcota.essentials.modules.tpa.history.TpaHistory;
import com.hanielcota.essentials.modules.tpa.repository.RequestRepository;
import com.hanielcota.essentials.paper.PlayerProvider;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * Application service for the teleport-request use cases: create, accept, deny, cancel, expire.
 *
 * <p>Sole responsibility: orchestration. It owns no state and renders no messages — it delegates
 * storage to {@link RequestRepository}, persistence to {@link TpaHistory} and player-facing notices
 * to {@link TpaNotifier}.
 */
public final class TeleportRequestService {

  private final ConfigHandle<TpaConfig> config;
  private final RequestRepository store;
  private final TpaNotifier notifier;
  private final PlayerProvider players;
  private final TpaRequestPolicy policy;
  private final TpaRequestRecorder recorder;
  private final TeleportRequestExecutor executor;

  public TeleportRequestService(
      @NonNull ConfigHandle<TpaConfig> config,
      @NonNull RequestRepository store,
      @NonNull TpaHistory history,
      @NonNull TpaNotifier notifier,
      @NonNull PlayerProvider players,
      @NonNull TpaProfileService profiles,
      @NonNull TpaBlockService blocks,
      @NonNull TpaContactService contacts) {
    this.config = config;
    this.store = store;
    this.notifier = notifier;
    this.players = players;
    this.policy = new TpaRequestPolicy(profiles, blocks);
    this.recorder = new TpaRequestRecorder(history, profiles, contacts);
    this.executor = new TeleportRequestExecutor(players);
  }

  /**
   * Registers a new request — replacing (and recording as cancelled) any request the requester
   * already had outstanding — and prompts the target. The previous target, if still online, is
   * notified that the request was replaced so they don't keep staring at a stale clickable prompt.
   */
  public Optional<TeleportRequest> create(
      @NonNull Player requester, @NonNull Player target, @NonNull TeleportRequestType type) {
    var targetId = target.getUniqueId();
    var requesterId = requester.getUniqueId();
    var accepted = this.policy.canCreate(requester, target, type);
    if (!accepted) {
      return Optional.empty();
    }

    var requesterName = requester.getName();

    var existing = this.store.outgoingOf(requesterId);
    existing.ifPresent(previous -> replacePrevious(previous, requesterId, requesterName));

    var snap = this.config.value();
    var lifetime = snap.requestExpiry();

    var requesterParticipant = Participant.of(requester);
    var targetParticipant = Participant.of(target);
    var request = TeleportRequest.open(requesterParticipant, targetParticipant, type, lifetime);

    this.store.add(request);
    this.recorder.recordCreated(requesterId, targetId);
    this.notifier.sendPrompt(target, request);

    return Optional.of(request);
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

  /**
   * Claims the accept synchronously: removes the request from the store and verifies both parties
   * are still online. Returns {@link AcceptResult#ACCEPTED} when the caller may proceed to {@link
   * #dispatchTeleport(TeleportRequest)}; the caller is expected to notify the accepter and
   * requester immediately on {@code ACCEPTED} so the chat reply does not wait for the async
   * teleport to finish.
   */
  public AcceptResult tryAccept(@NonNull TeleportRequest request) {
    if (!this.store.remove(request)) {
      return AcceptResult.NOT_FOUND;
    }

    var requesterId = request.requester().id();
    var requesterOnline = this.players.online(requesterId).isPresent();
    var targetId = request.target().id();
    var targetOnline = this.players.online(targetId).isPresent();

    if (!requesterOnline || !targetOnline) {
      this.recorder.recordTerminal(request, TeleportRequestStatus.CANCELLED);
      return AcceptResult.REQUESTER_OFFLINE;
    }

    return AcceptResult.ACCEPTED;
  }

  /**
   * Performs the async teleport for an already-claimed request and records the terminal status in
   * history. Resolves to {@code true} on success, {@code false} on teleport failure.
   */
  public CompletableFuture<Boolean> dispatchTeleport(@NonNull TeleportRequest request) {
    var pending = this.executor.execute(request);
    return pending.thenApply(execution -> this.recorder.recordExecution(request, execution));
  }

  /** Denies a request. Returns false when it was already resolved or expired. */
  public boolean deny(@NonNull TeleportRequest request) {
    return resolve(request, TeleportRequestStatus.DENIED);
  }

  /** Cancels a request (the requester withdrew it). */
  public void cancel(@NonNull TeleportRequest request) {
    resolve(request, TeleportRequestStatus.CANCELLED);
  }

  /** Expires a request and notifies the requester. Called by {@link TeleportRequestExpiry}. */
  public void expire(@NonNull TeleportRequest request) {
    if (!this.store.remove(request)) {
      return;
    }

    this.recorder.recordTerminal(request, TeleportRequestStatus.EXPIRED);

    this.notifier.notifyExpired(request);
  }

  /**
   * Cancels every request a player takes part in — used when they disconnect — and returns them so
   * the caller can notify the other party.
   */
  public List<TeleportRequest> cancelAllOf(@NonNull UUID player) {
    var affected = this.store.involving(player);
    for (TeleportRequest request : affected) {
      resolve(request, TeleportRequestStatus.CANCELLED);
    }
    return affected;
  }

  private void replacePrevious(
      @NonNull TeleportRequest previous, @NonNull UUID requesterId, @NonNull String requesterName) {
    resolve(previous, TeleportRequestStatus.CANCELLED);
    this.notifier.notifyPartnerLeft(previous, requesterId, requesterName);
  }

  /** Removes a request and writes its terminal state to history in one step. */
  private boolean resolve(@NonNull TeleportRequest request, @NonNull TeleportRequestStatus status) {
    if (!this.store.remove(request)) {
      return false;
    }

    this.recorder.recordTerminal(request, status);
    return true;
  }
}
