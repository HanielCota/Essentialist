package com.hanielcota.essentials.modules.list.service;

import lombok.NonNull;
import org.bukkit.Material;

/**
 * Materialised group view used internally by the list module. Decouples group resolution from the
 * raw {@code GroupDefinition} / {@code DefaultGroup} records so the renderer doesn't have to handle
 * "matched group" vs "default group" specially.
 */
public final class GroupResolution {

  public static final String DEFAULT_ID = "default";
  public static final int DEFAULT_PRIORITY = 0;

  private GroupResolution() {}

  public record Resolved(
      @NonNull String id, @NonNull String displayName, @NonNull Material material, int priority) {}
}
