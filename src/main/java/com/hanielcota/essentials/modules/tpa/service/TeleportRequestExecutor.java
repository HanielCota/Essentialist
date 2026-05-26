package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.modules.tpa.domain.Destination;
import com.hanielcota.essentials.modules.tpa.domain.TeleportRequest;
import com.hanielcota.essentials.paper.PlayerProvider;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;

@RequiredArgsConstructor
final class TeleportRequestExecutor {

  private final PlayerProvider players;

  private static TeleportExecution mapOutcome(Boolean success, @NonNull Location landing) {
    if (!Boolean.TRUE.equals(success)) {
      return TeleportExecution.failed();
    }

    var destination = Destination.of(landing);
    return TeleportExecution.success(destination);
  }

  CompletableFuture<TeleportExecution> execute(@NonNull TeleportRequest request) {
    var requesterId = request.requester().id();
    var requester = this.players.online(requesterId).orElse(null);

    var targetId = request.target().id();
    var target = this.players.online(targetId).orElse(null);

    if (requester == null || target == null) {
      return CompletableFuture.completedFuture(TeleportExecution.failed());
    }

    var type = request.type();
    var mover = type.mover(requester, target);
    var benchmark = type.destination(requester, target);
    var landing = benchmark.getLocation();

    var pending = mover.teleportAsync(landing);
    return pending.thenApply(success -> mapOutcome(success, landing));
  }
}
