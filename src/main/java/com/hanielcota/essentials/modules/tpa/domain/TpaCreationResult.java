package com.hanielcota.essentials.modules.tpa.domain;

import org.jspecify.annotations.Nullable;

/**
 * Result of creating a teleport request. Contains the new request and any previous request that was
 * replaced. Returned by {@link
 * com.hanielcota.essentials.modules.tpa.service.TeleportRequestService#create} so callers own the
 * notification side effects.
 */
public record TpaCreationResult(
    TeleportRequest request, @Nullable TeleportRequest replacedRequest) {}
