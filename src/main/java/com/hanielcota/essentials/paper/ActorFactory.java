package com.hanielcota.essentials.paper;

import io.github.hanielcota.commandframework.core.CommandActor;
import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * Wraps a {@link Player} as a {@link CommandActor} so notifiers and dual-message helpers can send
 * messages to a player using the same surface as a command sender. Extracted so collaborators that
 * only need this conversion do not have to depend on the full {@code PaperCommandFramework} (which
 * carries a large compile-time surface unrelated to messaging).
 */
public interface ActorFactory {

  CommandActor actorOf(@NonNull Player player);
}
