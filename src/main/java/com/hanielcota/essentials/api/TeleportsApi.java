package com.hanielcota.essentials.api;

import com.hanielcota.essentials.modules.teleport.domain.TeleportOutcome;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import org.bukkit.entity.Player;

/**
 * Teleport operations. Available when the {@code teleport} module is enabled.
 *
 * <p>Returned futures complete on Paper's teleport-completion thread; addons must hop back to the
 * main thread (or use the entity's region) before touching Bukkit API that is not scoped to the
 * moved player.
 */
public interface TeleportsApi {

  /** Teleports {@code from} to {@code to}'s current location. */
  CompletableFuture<TeleportOutcome> toPlayer(@NonNull Player from, @NonNull Player to);

  /** Teleports {@code subject} to the given coordinates within its current world. */
  CompletableFuture<TeleportOutcome> toPosition(
      @NonNull Player subject, double x, double y, double z);
}
