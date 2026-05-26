package com.hanielcota.essentials.modules.tpa.domain;

import java.util.UUID;
import lombok.NonNull;

/**
 * Aggregated contact stat: how many times {@code ownerId} successfully teleported a {@code
 * targetId} and when the last successful teleport happened. Distinct from {@link TpaFavorite}: a
 * favorite is an explicit player pick, a contact is derived from history.
 */
public record TpaContact(
    @NonNull UUID ownerId,
    @NonNull UUID targetId,
    @NonNull String targetName,
    long count,
    long lastUsedAtEpochMs) {}
