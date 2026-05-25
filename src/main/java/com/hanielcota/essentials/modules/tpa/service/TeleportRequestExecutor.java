package com.hanielcota.essentials.modules.tpa.service;

import com.hanielcota.essentials.modules.tpa.model.Destination;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequest;
import com.hanielcota.essentials.modules.tpa.model.TeleportRequestType;
import java.util.concurrent.CompletableFuture;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;

final class TeleportRequestExecutor {

  CompletableFuture<TeleportExecution> execute(@NonNull TeleportRequest request) {
    var requesterId = request.requester().id();
    var requester = Bukkit.getPlayer(requesterId);

    var targetId = request.target().id();
    var target = Bukkit.getPlayer(targetId);

    if (requester == null || target == null) {
      var failed = TeleportExecution.failed(AcceptResult.REQUESTER_OFFLINE);
      return CompletableFuture.completedFuture(failed);
    }

    var toTarget = request.type() == TeleportRequestType.TPA;
    var mover = toTarget ? requester : target;
    var benchmark = toTarget ? target : requester;
    var landing = benchmark.getLocation();

    var pending = mover.teleportAsync(landing);
    return pending.thenApply(success -> mapOutcome(success, landing));
  }

  private static TeleportExecution mapOutcome(Boolean success, @NonNull Location landing) {
    if (!Boolean.TRUE.equals(success)) {
      return TeleportExecution.failed(AcceptResult.TELEPORT_FAILED);
    }

    var destination = Destination.of(landing);
    return TeleportExecution.success(destination);
  }
}
