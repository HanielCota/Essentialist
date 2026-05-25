package com.hanielcota.essentials.modules.tpa.domain;

import java.util.UUID;
import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * A player as they take part in a teleport request: a stable {@link UUID} plus a name snapshot.
 *
 * <p>The id and the name always travel together, so wrapping them as one value object keeps every
 * request and history record free of loose, easily-mismatched parameters.
 */
public record Participant(UUID id, String name) {

  /** Snapshots an online player as a participant. */
  public static Participant of(@NonNull Player player) {
    return new Participant(player.getUniqueId(), player.getName());
  }
}
