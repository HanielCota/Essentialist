package com.hanielcota.essentials.modules.tpa.domain;

import java.util.UUID;
import lombok.NonNull;

/**
 * The player that a viewer is acting on from the target-action menu, plus which request type the
 * viewer reached the menu through (so the matching button can be highlighted as "recommended").
 */
public record TpaTargetSelection(
    @NonNull UUID targetId,
    @NonNull String targetName,
    @NonNull TeleportRequestType preferredType) {}
