package com.hanielcota.essentials.modules.mute.service;

import com.hanielcota.essentials.modules.mute.model.Mute;
import lombok.NonNull;

/**
 * Result of a {@link MuteService#mute(org.bukkit.entity.Player, String)} attempt. Lets the command
 * route the outcome to the notifier without inlining the domain branching.
 */
public sealed interface MuteOutcome {

  /** Target carries the {@code essentials.mute.exempt} permission. */
  record Exempt(@NonNull String targetName) implements MuteOutcome {}

  /** The raw duration argument was non-empty but could not be parsed. */
  record InvalidDuration(@NonNull String raw) implements MuteOutcome {}

  /** The mute was applied. */
  record Muted(@NonNull Mute mute) implements MuteOutcome {}
}
