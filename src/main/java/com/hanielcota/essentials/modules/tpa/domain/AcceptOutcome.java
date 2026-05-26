package com.hanielcota.essentials.modules.tpa.domain;

/**
 * Synchronous outcome of {@link TeleportRequestService#tryAccept(TeleportRequest)}: whether the
 * accept was claimed and the teleport may proceed. Teleport success / failure is reported
 * separately by {@link TeleportRequestService#dispatchTeleport(TeleportRequest)} so the accepter
 * and requester can be notified immediately, before the async teleport completes.
 */
public enum AcceptOutcome {
  ACCEPTED,
  NOT_FOUND,
  REQUESTER_OFFLINE
}
