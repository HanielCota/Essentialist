package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.modules.tpa.domain.TeleportExecution;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestStatus;
import com.hanielcota.essentials.modules.tpa.history.TpaHistory;
import com.hanielcota.essentials.modules.tpa.history.TpaHistoryEntry;
import java.time.Duration;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
final class TpaRequestRecorder {

  private final @NonNull TpaHistory history;
  private final @NonNull TpaProfileService profiles;
  private final @NonNull TpaContactService contacts;

  void recordCreated(@NonNull UUID requesterId, @NonNull UUID targetId) {
    this.profiles.recordSent(requesterId);
    this.profiles.recordReceived(targetId);
  }

  void recordTerminal(@NonNull TeleportRequest request, @NonNull TeleportRequestStatus status) {
    var entry = TpaHistoryEntry.of(request, status);

    this.history.push(entry);
  }

  boolean recordExecution(@NonNull TeleportRequest request, @NonNull TeleportExecution execution) {
    if (!execution.succeeded()) {
      recordTerminal(request, TeleportRequestStatus.CANCELLED);
      return false;
    }

    var destination = execution.optionalDestination().orElseThrow();
    var entry = TpaHistoryEntry.of(request, TeleportRequestStatus.ACCEPTED, destination);

    this.history.push(entry);
    recordAcceptedOutgoingStats(request, entry);
    return true;
  }

  void recordTeleportFailure(@NonNull TeleportRequest request) {
    recordTerminal(request, TeleportRequestStatus.CANCELLED);
  }

  void recordTeleportSuccess(
      @NonNull TeleportRequest request, @NonNull TeleportExecution execution) {
    var destination = execution.optionalDestination().orElseThrow();
    var entry = TpaHistoryEntry.of(request, TeleportRequestStatus.ACCEPTED, destination);
    this.history.push(entry);
    recordAcceptedOutgoingStats(request, entry);
  }

  private void recordAcceptedOutgoingStats(
      @NonNull TeleportRequest request, @NonNull TpaHistoryEntry entry) {
    var requesterId = request.requester().id();
    var target = request.target();
    var latencyMs = Math.max(0L, entry.resolvedAt() - entry.createdAt());
    var latency = Duration.ofMillis(latencyMs);

    this.profiles.recordAcceptedOutgoing(requesterId, latency);
    this.contacts.recordContact(requesterId, target.id(), target.name());
  }
}
