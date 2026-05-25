package com.hanielcota.essentials.modules.list.service;

import com.hanielcota.essentials.modules.list.service.GroupResolution.Resolved;
import java.util.UUID;
import lombok.NonNull;
import org.bukkit.Material;

/**
 * Player slot displayed in {@code /list}. Carries the resolved group so the renderer doesn't have
 * to re-evaluate permissions or look the player up again.
 */
public record PlayerEntry(
    @NonNull UUID id,
    @NonNull String name,
    @NonNull String groupId,
    @NonNull String groupDisplayName,
    @NonNull Material material,
    int groupPriority) {

  public static PlayerEntry of(@NonNull UUID id, @NonNull String name, @NonNull Resolved group) {
    return new PlayerEntry(
        id, name, group.id(), group.displayName(), group.material(), group.priority());
  }
}
