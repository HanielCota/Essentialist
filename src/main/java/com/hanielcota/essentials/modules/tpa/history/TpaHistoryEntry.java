package com.hanielcota.essentials.modules.tpa.history;

import com.hanielcota.essentials.modules.tpa.domain.Destination;
import com.hanielcota.essentials.modules.tpa.domain.Participant;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestStatus;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequestType;
import java.util.UUID;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * One resolved teleport request as stored in (and read back from) history.
 *
 * <p>Fully immutable — every field is a value type, including {@link Destination} — so an entry is
 * safe to hand straight to a background thread for persistence, with no defensive copying.
 *
 * <p>{@code destination} is the meeting point of an {@link TeleportRequestStatus#ACCEPTED} request;
 * it is {@code null} for every other terminal status.
 */
public record TpaHistoryEntry(
    UUID requester,
    Participant target,
    TeleportRequestType type,
    TeleportRequestStatus status,
    long createdAt,
    long resolvedAt,
    @Nullable Destination destination) {

  /** Builds an entry with no destination — a denied, expired or cancelled request. */
  public static TpaHistoryEntry of(
      @NonNull TeleportRequest request, @NonNull TeleportRequestStatus status) {
    return of(request, status, null);
  }

  /**
   * Builds an entry from a resolved request, stamping {@code resolvedAt} with the current time.
   * {@code destination} is the meeting point for an accepted request, or {@code null}.
   */
  public static TpaHistoryEntry of(
      @NonNull TeleportRequest request,
      @NonNull TeleportRequestStatus status,
      @Nullable Destination destination) {
    var requestWindow = request.window();
    var createdAtInstant = requestWindow.createdAt();
    var createdAtMillis = createdAtInstant.toEpochMilli();

    return new TpaHistoryEntry(
        request.requester().id(),
        request.target(),
        request.type(),
        status,
        createdAtMillis,
        System.currentTimeMillis(),
        destination);
  }
}
